package com.example.pidev.Entities;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Wallet implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "idWallet", length = 255)
  private Long idWallet;
  private Double Balance;


  @OneToMany(cascade = CascadeType.ALL,mappedBy = "walletBuyer")
  private Set<Transaction> transactionsBuyer;
  @OneToMany(cascade = CascadeType.ALL,mappedBy = "walletSeller")
  private Set<Transaction> transactionsSeller;


}
