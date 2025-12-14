package com.se.hub.modules.payment.service.impl;

import com.se.hub.modules.payment.entity.Wallet;
import com.se.hub.modules.payment.enums.WalletStatus;
import com.se.hub.modules.payment.repository.WalletRepository;
import com.se.hub.modules.payment.service.WalletService;
import com.se.hub.modules.profile.entity.Profile;
import com.se.hub.modules.profile.repository.ProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class WalletServiceImpl implements WalletService {

    WalletRepository walletRepository;
    ProfileRepository profileRepository;

    @Override
    @Transactional
    public Wallet createDefault(Profile profile) {
        if (profile == null) {
            throw new IllegalArgumentException("Profile cannot be null");
        }

        String profileId = profile.getId();
        if (profileId == null) {
            throw new IllegalArgumentException("Profile ID cannot be null");
        }

        return walletRepository.findByProfile_Id(profileId)
                .orElseGet(() -> {
                    Wallet wallet = Wallet.builder()
                            .profile(profile)
                            .status(WalletStatus.ACTIVE)
                            .depositCode(generateDepositCode(profile))
                            .build();
                    
                    Wallet savedWallet = walletRepository.save(wallet);
                    log.info("Wallet created successfully for profileId: {}", profileId);
                    return savedWallet;
                });
    }

    @Override
    @Transactional
    public Wallet ensureWallet(Profile profile) {
        if (profile == null || profile.getId() == null) {
            throw new IllegalArgumentException("Profile and Profile ID cannot be null");
        }
        
        Wallet wallet = walletRepository.findByProfile_Id(profile.getId()).orElse(null);
        
        if (wallet != null) {
            return wallet;
        }
        
        Wallet created = createDefault(profile);
        return created;
    }

    @Override
    @Transactional
    public int createWalletsForExistingProfiles() {
        log.info("Starting to create wallets for existing profiles without wallets");
        
        // Get all profiles
        List<Profile> allProfiles = profileRepository.findAll();
        int createdCount = 0;
        
        for (Profile profile : allProfiles) {
            if (!walletRepository.existsByProfile_Id(profile.getId())) {
                try {
                    createDefault(profile);
                    createdCount++;
                    log.debug("Created wallet for profileId: {}", profile.getId());
                } catch (Exception e) {
                    log.error("Failed to create wallet for profileId: {}. Error: {}", 
                            profile.getId(), e.getMessage(), e);
                }
            }
        }
        
        log.info("Completed creating wallets. Created {} wallets for existing profiles", createdCount);
        return createdCount;
    }

    /**
     * Generate deposit code from username if available and unique, otherwise use profile ID
     */
    private String generateDepositCode(Profile profile) {
        // Try to use username if available and unique
        if (profile.getUsername() != null && !profile.getUsername().trim().isEmpty()) {
            String username = profile.getUsername().trim();
            // Check if this username is already used as deposit code
            if (!walletRepository.existsByDepositCode(username)) {
                return username;
            }
        }
        
        // If username is not available or not unique, generate a unique code from profile ID
        // Use first 8 characters of profile ID + random suffix to ensure uniqueness
        String baseCode = profile.getId().substring(0, Math.min(8, profile.getId().length()));
        String depositCode = baseCode + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        
        // Ensure uniqueness
        int attempts = 0;
        while (walletRepository.existsByDepositCode(depositCode) && attempts < 10) {
            depositCode = baseCode + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
            attempts++;
        }
        
        if (attempts >= 10) {
            // Fallback: use full UUID if still not unique
            depositCode = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        }
        
        return depositCode;
    }
}

