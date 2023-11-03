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
//////l'achat ,tab des achats des users
public class Stock implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idStock", length = 255)
    private Long idStock;
    private Integer Quantity;
    private Double Price;
    private Date DateOfPurchase;

    @ManyToOne
    private Product product;

    @ManyToOne
    private Currencies currencies;

    @OneToOne
    @JsonIgnore
    private Wallet buyerWallet;




}
