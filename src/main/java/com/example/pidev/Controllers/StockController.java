package com.example.pidev.Controllers;

import com.example.pidev.Entities.*;
import com.example.pidev.Repositories.StockRepository;
import com.example.pidev.Repositories.TransactionRepository;
import com.example.pidev.Services.MarketPlaceService;
import com.example.pidev.Services.StockService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.Date;

@RestController
public class StockController {


    @Autowired
    StockService stockService;
    @Autowired
    StockRepository stockRepository;

    @Autowired
    TransactionRepository transactionRepository;




    @PostMapping("/{idMarket}")
    public ResponseEntity<Stock> performPurchase(
            Authentication authentication,
            @PathVariable Long idMarket,
            @RequestParam Integer quantity   ) {
        Stock purchasedStock = stockService.performPurchasetest(authentication, idMarket,quantity);

        if (purchasedStock != null) {
            // L'achat a réussi, renvoyer un objet Stock.
            return ResponseEntity.ok(purchasedStock);
        } else {
            // L'achat a échoué, renvoyer une réponse d'erreur.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }


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




//    @PostMapping("/performPurchase")
//    public ResponseEntity<String> performPurchase(
//            Authentication authentication,
//            @RequestParam Long buyerId,
//            @RequestParam Long sellerId,
//            @RequestParam Long productId,
//            @RequestParam Integer quantity) {
//
//        // Récupérer l'acheteur, le vendeur et le produit
//        User buyer = // récupérez l'acheteur à partir de buyerId
//                User seller = // récupérez le vendeur à partir de sellerId
//                Product product = // récupérez le produit à partir de productId
//
//        if (buyer == null || seller == null || product == null) {
//            return ResponseEntity.badRequest().body("Acheteur, vendeur ou produit introuvable.");
//        }
//
//        // Appeler la méthode performPurchase pour effectuer l'achat
//        Transaction transaction = stockService.performPurchase(buyer, seller, product, quantity);
//
//        if (transaction != null) {
//            return ResponseEntity.ok("Achat réussi.");
//        } else {
//            return ResponseEntity.badRequest().body("L'achat a échoué.");
//        }
//    }


}


