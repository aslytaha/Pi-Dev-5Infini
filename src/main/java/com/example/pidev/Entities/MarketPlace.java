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
public class MarketPlace implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idMarket", length = 255)
    private Long idMarket;
    private Integer Quantity;
    private Double unitPrice;
    private Double TotalPrice;
    private Date DateOfDeposit;
    @Enumerated(EnumType.STRING)
    private SellStatus Status;
    private Date SellDate;

    @ManyToOne
    private Product product;

    @ManyToOne
    private Currencies currencies;

    @OneToOne
    @JsonIgnore
    private Wallet sellerWallet;

}