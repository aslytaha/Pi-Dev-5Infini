package com.example.pidev.Services;

import com.example.pidev.Entities.Currencies;
import com.example.pidev.Entities.Product;
import com.example.pidev.Entities.Stock;
import com.example.pidev.Entities.User;
import com.example.pidev.Interfaces.IStockService;
import com.example.pidev.Repositories.CurrenciesRepository;
import com.example.pidev.Repositories.RawMaterialsRepository;
import com.example.pidev.Repositories.StockRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class StockService implements IStockService {
    @Autowired
    UserService userService;
    @Autowired

    RawMaterialsRepository rawMaterialsRepository;
    @Autowired

    CurrenciesRepository currenciesRepository;
    @Autowired

    StockRepository stockRepository;



//        @Override
//        public Stock newPurchase(Authentication authentication, Long idProduct, Long idCurrency, Integer quantity) {
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            String username = userDetails.getUsername();
//            User user = userService.retrieveUserByUsername(username);
//
//            Product rawMaterial = rawMaterialsRepository.findById(idProduct).get();
//            Currencies currencies = currenciesRepository.findById(idCurrency).get();
//
//            if (user != null) {
//                Stock stock = new Stock();
//                stock.setSellerWallet(user.getUserWallet());
//                stock.setQuantity(quantity);
//                System.out.println("hhhhhhhhhhh");
//
//
//                if (rawMaterial != null) {
//                    stock.setProduct(rawMaterial);
//                    Double price = rawMaterial.getUnitPrice() * quantity;
//                    stock.setPrice(price);
//                } else if (currencies != null) {
//                    stock.setCurrencies(currencies);
//                    stock.setQuantity(quantity);
//                    Double price = rawMaterial.getUnitPrice() * quantity;
//                    stock.setPrice(price);
//                }
//
//                Date dateActuelle = new Date();
//                stock.setDateOfPurchase(dateActuelle);
//                System.out.println("kkkkkkkkk");
//
//                return stockRepository.save(stock);
//            }
//            System.out.println("kkkkkkkkk");
//            return null;
//        }

    @Override
    public Stock newPurchase(Authentication authentication, Long idProduct, Long idCurrency, Integer quantity) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User user = userService.retrieveUserByUsername(username);

        Product rawMaterial = null;
        Currencies currencies = null;

        if (idProduct != null) {
            rawMaterial = rawMaterialsRepository.findById(idProduct).orElse(null);
        } else if (idCurrency != null) {
            currencies = currenciesRepository.findById(idCurrency).orElse(null);
        }

        if (user != null) {
            if (rawMaterial != null || currencies != null) {
                Stock stock = new Stock();
                stock.setSellerWallet(user.getUserWallet());
                stock.setQuantity(quantity);

                if (rawMaterial != null) {
                    stock.setProduct(rawMaterial);
                    Double price = rawMaterial.getUnitPrice() * quantity;
                    stock.setPrice(price);
                } else if (currencies != null) {
                    stock.setCurrencies(currencies);
                    Double price = currencies.getExchangeRate() * quantity;
                    stock.setPrice(price);
                }

                Date dateActuelle = new Date();
                stock.setDateOfPurchase(dateActuelle);

                return stockRepository.save(stock);
            }
        }
        return null;
    }




    }

