package com.example.pidev.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
  @JsonIgnore
  private  Wallet walletBuyer;

  @ManyToOne
  @JsonIgnore
  private  Wallet walletSeller;
}
