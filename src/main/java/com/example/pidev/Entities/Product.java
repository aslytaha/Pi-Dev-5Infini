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
public class Product implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "idProduct", length = 255)
  private Long idProduct;
  private String productName;
  private Double unitPrice;
  private String refProduct;
  @Enumerated(EnumType.STRING)
  private productCat Category;



}
