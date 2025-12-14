package com.se.hub.modules.payment.repository;

import com.se.hub.modules.payment.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, String> {
    boolean existsByProfile_Id(String profileId);
    Optional<Wallet> findByProfile_Id(String profileId);
    boolean existsByDepositCode(String depositCode);
    Optional<Wallet> findByDepositCode(String depositCode);
}

