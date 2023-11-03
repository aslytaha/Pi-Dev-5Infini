package com.example.pidev.Services;

import com.example.pidev.Controllers.MarketPlaceController;
import com.example.pidev.Entities.*;
import com.example.pidev.Interfaces.IMarketPlaceService;
import com.example.pidev.Repositories.CurrenciesRepository;
import com.example.pidev.Repositories.MarketPlaceRepository;
import com.example.pidev.Repositories.RawMaterialsRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
@NoArgsConstructor
public class MarketPlaceService implements IMarketPlaceService {
    @Autowired
    UserService userService;
    @Autowired
    RawMaterialsRepository rawMaterialsRepository;
    @Autowired
    CurrenciesRepository currenciesRepository;
    @Autowired
    StockService stockService;

    @Autowired
    MarketPlaceRepository marketPlaceRepository;


    public MarketPlace DepositItemForSale(Authentication authentication, Long idProduct, Long idCurrency, Integer quantity, Double Unitprice) throws Exception {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User seller = userService.retrieveUserByUsername(username);

        if (idProduct != null) {
            Product product = rawMaterialsRepository.findById(idProduct).orElse(null);
            if (product != null) {
                if (stockService.hasProductInStock(seller, product, quantity, null)) {
                    MarketPlace marketPlace = new MarketPlace();
                    marketPlace.setSellerWallet(seller.getUserWallet());
                    marketPlace.setProduct(product);
                    marketPlace.setQuantity(quantity);
                    marketPlace.setUnitPrice(Unitprice);
                    marketPlace.setTotalPrice(Unitprice * quantity);
                    marketPlace.setDateOfDeposit(new Date());
                    marketPlace.setStatus(SellStatus.ForSale);
                    marketPlace.setSellDate(null);
                    return marketPlaceRepository.save(marketPlace);
                } else {
                    throw new Exception("L'utilisateur ne possède pas suffisamment de ce produit.");
                }
            }
        } else if (idCurrency != null) {
            Currencies currencies = currenciesRepository.findById(idCurrency).orElse(null);
            if (currencies != null) {
                if (stockService.hasCurrenciesInStock(seller, currencies, quantity, null)) {
                    MarketPlace marketPlace = new MarketPlace();
                    marketPlace.setSellerWallet(seller.getUserWallet());
                    marketPlace.setCurrencies(currencies);
                    marketPlace.setQuantity(quantity);
                    marketPlace.setUnitPrice(Unitprice);
                    marketPlace.setTotalPrice(Unitprice * quantity);
                    marketPlace.setDateOfDeposit(new Date());
                    marketPlace.setStatus(SellStatus.ForSale);
                    marketPlace.setSellDate(null);
                    return marketPlaceRepository.save(marketPlace);
                } else {
                    throw new Exception("L'utilisateur ne possède pas suffisamment de ces devises.");
                }
            }
        }

        throw new IllegalArgumentException("Type de produit non pris en charge");
    }

    public static final String FOR_SALE = "ForSale";
    @Override

    public List<MarketPlace> retrieveAvailableSales() {
        List<MarketPlace> marketPlaces = marketPlaceRepository.findAll();

        List<MarketPlace> availableMP = marketPlaces.stream()
                .filter(MP -> SellStatus.ForSale.equals(MP.getStatus()))
                .collect(Collectors.toList());

        return availableMP;
    }

    @Override
    public List<MarketPlace> retrieveSoldItems() {
        List<MarketPlace> marketPlaces = marketPlaceRepository.findAll();

        List<MarketPlace> soldMP = marketPlaces.stream()
                .filter(MP -> SellStatus.Sold.equals(MP.getStatus()))
                .collect(Collectors.toList());

        return soldMP;
    }
}