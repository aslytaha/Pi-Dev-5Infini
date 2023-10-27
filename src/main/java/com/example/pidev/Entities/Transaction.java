package com.example.pidev.Entities;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Transaction implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "idTransaction", length = 255)
  private Long idTransaction;
  private Double totalPrice;
  private Date transactionDate;
  @ManyToOne
  private  Wallet walletBuyer;
  @ManyToOne
  private  Wallet walletSeller;
}
