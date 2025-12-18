package com.se.hub.modules.user.service;

import com.se.hub.modules.user.entity.User;
import com.se.hub.modules.user.enums.UserStatus;
import com.se.hub.modules.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Service to sync/create user from FTES JWT token
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserSyncService {

    private final UserRepository userRepository;
    private final EntityManager entityManager;
    
    // Locks for each userId to prevent concurrent creation
    private final ConcurrentHashMap<String, Lock> userLocks = new ConcurrentHashMap<>();

    /**
     * Get or create user from JWT token
     * If user doesn't exist, create with default role based on FTES JWT scope
     * Uses per-user locking to prevent concurrent creation
     * 
     * @param jwt JWT token from FTES
     * @return User entity
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public User getOrCreateUser(Jwt jwt) {
        String userId = getUserIdFromJwt(jwt);
        if (userId == null) {
            throw new IllegalArgumentException("JWT token does not contain userId or sub claim");
        }

        // First check without lock for performance (use optimized query without profile)
        return userRepository.findByIdWithRole(userId)
                .orElseGet(() -> createUserWithLock(userId, jwt));
    }
    
    /**
     * Create user with per-user lock to prevent race conditions
     */
    private User createUserWithLock(String userId, Jwt jwt) {
        // Get or create lock for this specific userId
        Lock lock = userLocks.computeIfAbsent(userId, k -> new ReentrantLock());
        
        lock.lock();
        try {
            // Double-check after acquiring lock (use optimized query without profile)
            return userRepository.findByIdWithRole(userId)
                    .orElseGet(() -> {
                        try {
                            return createUserFromJwt(userId, jwt);
                        } catch (ObjectOptimisticLockingFailureException | DataIntegrityViolationException e) {
                            // Another thread created the user, fetch it
                            log.debug("User {} was created by another thread, fetching...", userId);
                            return userRepository.findByIdWithRole(userId)
                                    .orElseThrow(() -> new RuntimeException("Failed to create or find user: " + userId, e));
                        }
                    });
        } finally {
            lock.unlock();
            // Clean up lock if no longer needed (optional, for memory management)
            userLocks.remove(userId, lock);
        }
    }

    /**
     * Get user ID from JWT token
     */
    private String getUserIdFromJwt(Jwt jwt) {
        Object userId = jwt.getClaims().get("userId");
        if (userId == null) {
            userId = jwt.getClaims().get("sub"); // Subject claim
        }
        if (userId == null) {
            userId = jwt.getClaims().get("id");
        }
        return userId != null ? userId.toString() : null;
    }

    /**
     * Create user from JWT token with appropriate role
     * Uses REQUIRES_NEW propagation to create a new transaction, so if save fails,
     * the transaction can be rolled back without affecting the parent transaction
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public User createUserFromJwt(String userId, Jwt jwt) {
        log.info("Creating new user with ID: {}", userId);
        
        // Double-check user doesn't exist (might have been created by another thread)
        Optional<User> existingUser = userRepository.findByIdWithRole(userId);
        if (existingUser.isPresent()) {
            log.debug("User {} already exists, fetching...", userId);
            return existingUser.get();
        }
        
        // Get role from JWT scope - handle both string and list formats
        String roleName = determineRoleNameFromScope(jwt);
        com.se.hub.modules.user.entity.Role role = entityManager.getReference(
                com.se.hub.modules.user.entity.Role.class, roleName); // Get managed reference
        
        // Create new user instance with ID set (no @GeneratedValue, so can set directly)
        User user = User.builder()
                .id(userId) // Set ID from JWT token
                .status(UserStatus.ACTIVE)
                .role(role)
                .build();
        
        try {
            // Use repository.save() which handles both persist and merge automatically
            // It will check if entity exists and merge if needed, or persist if new
            User savedUser = userRepository.save(user);
            userRepository.flush(); // Flush to ensure user is persisted
            
            log.info("Created user with ID: {} and role: {}", userId, roleName);
            
            return savedUser;
        } catch (DataIntegrityViolationException e) {
            // User already exists (unique constraint violation)
            // Rollback this transaction and let parent transaction handle fetching
            log.debug("User {} already exists (constraint violation), will fetch in parent transaction", userId);
            throw e; // Re-throw to rollback this transaction
        } catch (Exception e) {
            // Other errors - rollback this transaction and throw
            log.error("Failed to create user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to create user: " + userId, e);
        }
    }
    
    /**
     * Determine role name from FTES JWT scope
     * Scope can be either a string (e.g., "ROLE_ADMIN", "ROLE_STUDENT") or a list
     * Logic: If scope is "ROLE_ADMIN" → role = "ADMIN", otherwise → role = "USER"
     */
    private String determineRoleNameFromScope(Jwt jwt) {
        // Try to get scope as string first (most common format)
        String scopeString = jwt.getClaimAsString("scope");
        String admin = "ADMIN";
        if (scopeString != null && !scopeString.trim().isEmpty()) {
            // Check if scope is ROLE_ADMIN
            if (scopeString.equalsIgnoreCase("ROLE_ADMIN") || scopeString.equalsIgnoreCase(admin)) {
                log.debug("Scope is ADMIN: {}", scopeString);
                return admin;
            }
            // All other scopes (ROLE_STUDENT, ROLE_INSTRUCTOR, etc.) → USER
            log.debug("Scope is not ADMIN, defaulting to USER: {}", scopeString);
            return "USER";
        }
        
        // Fallback: Try to get scope as list (for backward compatibility)
        List<String> scopes = jwt.getClaimAsStringList("scope");
        if (scopes != null && !scopes.isEmpty()) {
            // Check if any scope indicates admin role
            boolean isAdmin = scopes.stream()
                    .anyMatch(scope -> scope != null && (
                            scope.equalsIgnoreCase("ROLE_ADMIN") ||
                            scope.equalsIgnoreCase(admin) ||
                            scope.contains(admin)
                    ));
            
            if (isAdmin) {
                log.debug("Found ADMIN in scope list: {}", scopes);
                return admin;
            }
            log.debug("No ADMIN found in scope list, defaulting to USER: {}", scopes);
            return "USER";
        }
        
        // No scope found → default to USER
        log.debug("No scope found in JWT, defaulting to USER");
        return "USER";
    }

}

