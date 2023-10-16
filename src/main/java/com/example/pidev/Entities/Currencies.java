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
public class Currencies implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "idCurrency", length = 255)
  private Long idCurrency;
  private String Symbol;
  private Double exchangeRate;
  private String countryName;
  private String currencyUnit;
  private Date lastUpdated;
  @Enumerated(EnumType.STRING)
  private productCat Category;
}
