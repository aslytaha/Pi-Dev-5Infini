package com.example.pidev.Repositories;

import com.example.pidev.Entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface StockRepository extends JpaRepository<Stock,Long> {
//    @Query("SELECT s FROM Stock s WHERE s.buyerWallet = :sellerWallet AND s.product = :product")
//    Stock findBySellerWalletAndProduct(@Param("sellerWallet") Wallet sellerWallet, @Param("product") Product product);
//
//
//    @Query("SELECT s FROM Stock s WHERE s.buyerWallet = :sellerWallet AND s.currencies = :currencies")
//    Stock findBySellerWalletAndCurrencies(@Param("sellerWallet") Wallet sellerWallet, @Param("currencies") Currencies currencies);
//

//    Stock findBySellerAndProduct(User seller, Product product);

    List<Stock> findAllByBuyerWalletAndCurrencies(Wallet userWallet, Currencies currencies);
    List<Stock> findAllByBuyerWalletAndProduct(Wallet userWallet, Product product);

    Stock findByBuyerWalletAndProduct(Wallet userWallet,Product product);
    Stock findByBuyerWalletAndCurrencies(Wallet userWallet,Currencies currencies);
}

//    Stock NewPurchase(Long idProduct ,Long idCurrency, )

