package com.example.pidev.Controllers;

import com.example.pidev.Entities.Stock;
import com.example.pidev.Services.StockService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class StockController {


    @Autowired
    StockService stockService;


    @PostMapping("/purchase")
    public ResponseEntity<String> newPurchase(
            Authentication authentication,
            @RequestParam(required = false) Long idProduct,
            @RequestParam(required = false) Long idCurrency,
            @RequestParam Integer quantity) {

        if ((idProduct == null && idCurrency == null) || (idProduct != null && idCurrency != null)) {
            return ResponseEntity.badRequest().body("Spécifiez soit Matieres PREMIERES, soit Devises, mais pas les deux.");
        }

        Stock stock = null;
        if (idProduct != null) {
            stock = stockService.newPurchase(authentication, idProduct, null, quantity);
        } else if (idCurrency != null) {
            stock = stockService.newPurchase(authentication, null, idCurrency, quantity);
        }

        if (stock != null) {
            return ResponseEntity.ok("L'achat a été effectué avec succès.");
        } else {
            return ResponseEntity.badRequest().body("L'achat n'a pas pu être effectué.");
        }
    }
}


