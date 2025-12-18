package com.se.hub.modules.profile.repository;

import com.se.hub.modules.profile.entity.Follow;
import com.se.hub.modules.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, String> {
    
    /**
     * Check if follower is following user
     */
    boolean existsByFollowerAndFollowing(User follower, User following);
    
    /**
     * Find follow relationship by follower and following
     */
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);
    
    /**
     * Get all users that a user is following (following list)
     */
    @Query("SELECT f.following FROM Follow f WHERE f.follower.id = :userId")
    Page<User> findFollowingByUserId(@Param("userId") String userId, Pageable pageable);
    
    /**
     * Get all users that follow a user (followers list)
     */
    @Query("SELECT f.follower FROM Follow f WHERE f.following.id = :userId")
    Page<User> findFollowersByUserId(@Param("userId") String userId, Pageable pageable);
    
    /**
     * Count how many users a user is following
     */
    long countByFollower(User follower);
    
    /**
     * Count how many followers a user has
     */
    long countByFollowing(User following);
    
    /**
     * Get mutual friends (users that both current user and target user follow each other)
     * Mutual friend = user A follows user B AND user B follows user A

     * Query explanation:
     * - f1: current user (userId) follows someone (f1.following = mutual friend)
     * - f2: that someone (f1.following) follows current user back (f2.following = userId)

     * Using JPQL with proper WHERE clause for mutual follow relationship
     */
    @Query("SELECT DISTINCT f1.following FROM Follow f1 " +
           "WHERE f1.follower.id = :userId " +
           "AND EXISTS (SELECT 1 FROM Follow f2 WHERE f2.follower = f1.following AND f2.following.id = :userId)")
    List<User> findMutualFriends(@Param("userId") String userId);
}

