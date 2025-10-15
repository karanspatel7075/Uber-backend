package com.example.Navio.service;

import com.example.Navio.model.Wallet;
import com.example.Navio.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WalletService {

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet getWalletDetails(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
    }

    public Wallet addFunds(Long userId, Double amount) {
        Wallet wallet = getWalletDetails(userId);
        wallet.setBalance(wallet.getBalance() + amount);
        wallet.getTransactionHistory().add("Added " + amount);
        return walletRepository.save(wallet);
    }

    public Wallet debtFunds(Long userId, Double amount) {
        Wallet wallet = getWalletDetails(userId);
        if(wallet.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }

        wallet.setBalance(wallet.getBalance() - amount);
        wallet.getTransactionHistory().add("Debited :" + amount);
        return walletRepository.save(wallet);
    }
}
