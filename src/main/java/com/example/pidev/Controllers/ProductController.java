package com.example.pidev.Controllers;

import com.example.pidev.Entities.Product;
import com.example.pidev.Services.ProductServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/Product")
public class ProductController {

    @Autowired
    private ProductServices productServices;

    @PostMapping("/create-product")
    public Product addCours(@RequestBody Product product) {
        return productServices.addProduct(product);
    }





    @GetMapping("/getPriceData/{symbol}")
    public ResponseEntity<Map<String, Object>> getPriceData(
            @PathVariable String symbol,
            @RequestParam String interval
    ) {
        Map<String, Object> priceData = productServices.getPriceDataForSymbol(symbol, interval);
        if (priceData.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } else {
            return ResponseEntity.ok(priceData);
        }
    }

    @PostMapping("/updatePrices")
    public ResponseEntity<String> updatePrices() {
        try {
            // Appelez la méthode updateProductPrices dans votre service
            productServices.updateProductPrices();
            return ResponseEntity.ok("Mise à jour des prix effectuée avec succès.");
        } catch (Exception e) {
            // Gérez les erreurs, par exemple en renvoyant une réponse d'erreur
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la mise à jour des prix : " + e.getMessage());
        }
    }

}
