package com.example.pidev.Services;

import com.example.pidev.Entities.Product;
import com.example.pidev.Repositories.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class ProductServices {

    @Autowired
    private ProductRepository productRepository ;

   
    public Product addProduct(Product p) {
        return  productRepository.save(p);
    }






    public Map<String, Object> getPriceDataForSymbol(String symbol, String interval) {
        String apiKey = "OGP9RUY6CYZ9CXAV"; // Remplacez par votre clé API
        String url = "https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=" + symbol + "&interval=" + interval + "&apikey=" + apiKey;

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            String responseData = response.getBody();
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> result = new LinkedHashMap<>();
                JsonNode rootNode = objectMapper.readTree(responseData);
                result.put("Meta Data", rootNode.get("Meta Data"));
                result.put("Time Series (" + interval + ")", rootNode.get("Time Series (" + interval + ")"));
                return result;
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return Collections.emptyMap();
            }
        } else {
            // Gérez les erreurs ou renvoyez un résultat par défaut
            return Collections.emptyMap();
        }
    }



    public Double extractLastPrice(Map<String,Object> data) {
        // Remplacez "Time Series (5min)" par la clé correspondante dans vos données

        Map<String,Object> timeSeriesData = (Map<String, Object>) data.get("Time Series (1min)");

        if (timeSeriesData != null && !timeSeriesData.isEmpty()) {
            // Obtenir les clés (les horodatages) sous forme de liste
            List<String> timestamps = new ArrayList<>(timeSeriesData.keySet());

            // Trier les horodatages (clés) par ordre décroissant
            Collections.sort(timestamps, Collections.reverseOrder());

            // Obtenir la dernière horodatage (la plus récente)
            String lastTimestamp = timestamps.get(0);

            // Obtenir les données de prix pour la dernière horodatage
            Map<String, Object> lastPriceData = (Map<String, Object>) timeSeriesData.get(lastTimestamp);

            if (lastPriceData != null) {
                // Extraire le prix de clôture (ou d'ouverture, ou autre, selon vos besoins)
                String lastPrice = (String) lastPriceData.get("4. close");
                if (lastPrice != null) {
                    return Double.parseDouble(lastPrice);
                }
            }
        }

        return null; // Si les données sont introuvables ou si une erreur survient
    }

    //@Scheduled(fixedRate = 60000)
    public void updateProductPrices() {
        // Récupérez les données de l'API Alpha Vantage pour la dernière valeur du prix
        Map<String, Object> priceData = getPriceDataForSymbol("GOLD", "1min");

        // Extrayez la dernière valeur du prix
        Double lastPrice = extractLastPrice(priceData);

        if (lastPrice != null) {
            // Mettez à jour tous les produits avec la dernière valeur du prix
            List<Product> products = productRepository.findByProductName("GOLD");
            for (Product product : products) {
                product.setUnitPrice(lastPrice.toString()); // Convertissez en String si nécessaire
            }
            productRepository.saveAll(products);
        }
    }



}
