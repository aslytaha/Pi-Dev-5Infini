package com.example.pidev.Controllers;


import com.example.pidev.Entities.MarketPlace;
import com.example.pidev.Entities.User;
import com.example.pidev.Services.MarketPlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
    public class MarketPlaceController {


        @Autowired
        MarketPlaceService marketPlaceService;


    @GetMapping("/AvailableSales")
    public List<MarketPlace> getAvailableSales() {
        List<MarketPlace> marketPlaces = marketPlaceService.retrieveAvailableSales();
        return marketPlaces;

    }
    @GetMapping("/SoldItems")
    public List<MarketPlace> getSoldItems() {
        List<MarketPlace> marketPlaces = marketPlaceService.retrieveSoldItems();
        return marketPlaces;

    }

        @PostMapping("/deposit")
        public ResponseEntity<?> depositItemForSale(
                Authentication authentication,
                @RequestParam(required = false) Long productId,
                @RequestParam(required = false) Long currencyId,
                @RequestParam Integer quantity,
                @RequestParam Double unitPrice
        ) throws Exception {


            MarketPlace marketPlace = marketPlaceService.DepositItemForSale(authentication, productId, currencyId, quantity, unitPrice);
            return ResponseEntity.ok(marketPlace);
        }
    }

