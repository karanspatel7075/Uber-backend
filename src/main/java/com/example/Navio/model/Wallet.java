package com.example.Navio.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    private double balance;

    @ElementCollection
    @CollectionTable(name = "wallet_transactions", joinColumns = @JoinColumn(name = "wallet_id"))
    @Column(name = "transaction")
    private List<String> transactionHistory = new ArrayList<>();
}
