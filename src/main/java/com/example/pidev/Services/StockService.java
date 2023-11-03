package com.example.pidev.Services;

import com.example.pidev.Entities.*;
import com.example.pidev.Interfaces.IStockService;
import com.example.pidev.Repositories.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.example.pidev.Entities.Stock;



import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    MarketPlaceRepository marketPlaceRepository;


    public boolean hasProductInStock(User user, Product product, int quantity, StringBuilder errorMessage) {
        if (user == null || product == null) {
            errorMessage.append("Invalid user or product");
            return false;
        }

        List<Stock> userStock = stockRepository.findAllByBuyerWalletAndProduct(user.getUserWallet(), product);

        int availableQuantity = userStock.stream().mapToInt(Stock::getQuantity).sum();

        if (availableQuantity >= quantity) {
            return true; // Sufficient stock
        } else {
            errorMessage.append("Insufficient stock for the specified product");
            return false;
        }
    }


    public boolean hasCurrenciesInStock(User user, Currencies currencies, double amount, StringBuilder errorMessage) {
        if (user == null || currencies == null) {
            errorMessage.append("Invalid user or currency");
            return false;
        }

        List<Stock> userStock = stockRepository.findAllByBuyerWalletAndCurrencies(user.getUserWallet(), currencies);

        double availableAmount = userStock.stream().mapToDouble(Stock::getQuantity).sum();

        if (availableAmount >= amount) {
            return true; // Sufficient stock
        } else {
            errorMessage.append("Insufficient amount of the specified currency");
            return false;
        }
    }


    @Transactional
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
                // Calculez le montant total de l'achat
                Double purchaseAmount = 0.0;

                if (rawMaterial != null) {
                    purchaseAmount = rawMaterial.getUnitPrice() * quantity;
                } else if (currencies != null) {
                    purchaseAmount = currencies.getExchangeRate() * quantity;
                }

                // Vérifiez si l'utilisateur a suffisamment d'argent dans son portefeuille
                Wallet userWallet = user.getUserWallet();
                if (userWallet.getBalance() >= purchaseAmount) {
                    Stock existingStock = null;

                    if (rawMaterial != null) {
                        existingStock = stockRepository.findByBuyerWalletAndProduct(userWallet, rawMaterial);
                    } else if (currencies != null) {
                        existingStock = stockRepository.findByBuyerWalletAndCurrencies(userWallet, currencies);
                    }

                    // Si un stock existant est trouvé
                    Stock stock = null;
                    if (existingStock != null) {
                        // Augmentez simplement la quantité et le prix
                        existingStock.setQuantity(existingStock.getQuantity() + quantity);
                        existingStock.setPrice(existingStock.getPrice() + purchaseAmount);

                        // Mise à jour de la date d'achat si nécessaire
                        existingStock.setDateOfPurchase(new Date());

                        // Enregistrez la mise à jour du stock dans la base de données
                        stockRepository.save(existingStock);
                    } else {
                        // Si aucun stock existant n'est trouvé, créez un nouveau stock
                        stock = new Stock();
                        stock.setBuyerWallet(userWallet);
                        stock.setQuantity(quantity);

                        if (rawMaterial != null) {
                            stock.setProduct(rawMaterial);
                        } else if (currencies != null) {
                            stock.setCurrencies(currencies);
                        }

                        stock.setPrice(purchaseAmount);

                        Date dateActuelle = new Date();
                        stock.setDateOfPurchase(dateActuelle);

                        // Enregistrez le nouveau stock dans la base de données
                        stockRepository.save(stock);
                    }

                    // Créez la transaction
                    Transaction transaction = new Transaction();
                    transaction.setTotalPrice(purchaseAmount);
                    transaction.setTransactionDate(new Date());
                    transaction.setWalletBuyer(userWallet);

                    // Enregistrez la transaction dans la base de données
                    transactionRepository.save(transaction);

                    // Mettez à jour le solde du portefeuille de l'acheteur (déduction du montant d'achat)
                    userWallet.setBalance(userWallet.getBalance() - purchaseAmount);
                    walletRepository.save(userWallet);

                    return existingStock != null ? existingStock : stock;
                }
            }
        }
        return null;
    }




    @Transactional
    public Stock performPurchasetest(Authentication authentication, Long idMarket, Integer desiredQuantity) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User buyer = userService.retrieveUserByUsername(username);

        if (buyer == null) {
            // L'acheteur n'a pas été trouvé
            return null;
        }

        MarketPlace market = marketPlaceRepository.findById(idMarket).orElse(null);

        if (market == null || market.getStatus() != SellStatus.ForSale) {
            // Le marché n'a pas été trouvé ou l'article n'est pas disponible
            return null;
        }

        Wallet sellerWallet = market.getSellerWallet();

        if (sellerWallet == null) {
            // Le portefeuille du vendeur n'a pas été trouvé
            return null;
        }

        Product product = market.getProduct();
        Currencies currencies = market.getCurrencies();
        int availableQuantity = market.getQuantity();
        double unitPrice = market.getUnitPrice();
        double totalPrice = unitPrice * desiredQuantity;

        if (desiredQuantity > availableQuantity) {
            // La quantité souhaitée est supérieure à la quantité disponible.
            return null;
        }

        // Vérifiez si l'acheteur possède déjà ce produit.
        Stock existingStock = null;

        if (product != null) {
            existingStock = stockRepository.findByBuyerWalletAndProduct(buyer.getUserWallet(), product);
        } else if (currencies != null) {
            existingStock = stockRepository.findByBuyerWalletAndCurrencies(buyer.getUserWallet(), currencies);
        }

        if (existingStock != null) {
            // L'acheteur possède déjà le produit, augmentez la quantité et le prix.
            existingStock.setQuantity(existingStock.getQuantity() + desiredQuantity);
            existingStock.setPrice(existingStock.getPrice() + totalPrice);

            // Mettez à jour le solde de l'acheteur et du vendeur.
            buyer.getUserWallet().setBalance(buyer.getUserWallet().getBalance() - totalPrice);
            sellerWallet.setBalance(sellerWallet.getBalance() + totalPrice);

            // Décrémentez la quantité mise en vente par le vendeur.
            market.setQuantity(availableQuantity - desiredQuantity);

            // Enregistrez les modifications de l'objet Stock existant dans la base de données.
            stockRepository.save(existingStock);

            // Mettez à jour l'état de l'article sur le marché.
            if (availableQuantity - desiredQuantity == 0) {
                market.setStatus(SellStatus.Sold);
            }
            marketPlaceRepository.save(market);

            return existingStock;
        } else {
            // L'acheteur n'a pas encore ce produit, créez un nouvel objet Stock.
            Stock stock = new Stock();
            stock.setBuyerWallet(buyer.getUserWallet());
            stock.setProduct(product);
            stock.setCurrencies(currencies);
            stock.setQuantity(desiredQuantity);
            stock.setPrice(totalPrice);
            Date dateActuelle = new Date();
            stock.setDateOfPurchase(dateActuelle);
            buyer.getUserWallet().setBalance(buyer.getUserWallet().getBalance() - totalPrice);
            sellerWallet.setBalance(sellerWallet.getBalance() + totalPrice);

            // Mettez à jour la quantité mise en vente par le vendeur.
            market.setQuantity(availableQuantity - desiredQuantity);

            // Enregistrez le nouvel objet Stock dans la base de données.
            stockRepository.save(stock);

            // Enregistrez une transaction entre l'acheteur et le vendeur.
            Transaction transaction = new Transaction();
            transaction.setWalletBuyer(buyer.getUserWallet());
            transaction.setWalletSeller(sellerWallet);
            transaction.setTotalPrice(totalPrice);
            transaction.setTransactionDate(new Date());

            // Enregistrez la transaction dans la base de données.
            transactionRepository.save(transaction);

            // Mettez à jour l'état de l'article sur le marché.
            if (availableQuantity - desiredQuantity == 0) {
                market.setStatus(SellStatus.Sold);
            }
            marketPlaceRepository.save(market);

            return stock;
        }
    }
}


//    @Transactional
//    public Stock performPurchase(Authentication authentication, Long idMarket,Integer desiredQuantity) {
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        String username = userDetails.getUsername();
//        User buyer = userService.retrieveUserByUsername(username);
//
//        if (buyer == null) {
//            // L'acheteur n'a pas été trouvé
//            return null;
//        }
//
//        MarketPlace market = marketPlaceRepository.findById(idMarket).orElse(null);
//
//        if (market == null || market.getStatus() != SellStatus.ForSale) {
//            // Le marché n'a pas été trouvé ou l'article n'est pas disponible
//            return null;
//        }
//
//        Wallet sellerWallet = market.getSellerWallet();
//
//        if (sellerWallet == null) {
//            // Le portefeuille du vendeur n'a pas été trouvé
//            return null;
//        }
//
//        Product product = market.getProduct();
//        Currencies currencies = market.getCurrencies();
//        int availableQuantity = market.getQuantity();
//        double totalPrice = market.getUnitPrice()*desiredQuantity;
//
//        if (desiredQuantity > availableQuantity) {
//            // La quantité souhaitée est supérieure à la quantité disponible.
//            return null;
//        }
//
//        // Vérifiez si l'acheteur possède déjà ce produit.
//        Stock existingStockP = stockRepository.findByBuyerWalletAndProduct(buyer.getUserWallet(), product);
//        Stock existingStockD = stockRepository.findByBuyerWalletAndCurrencies(buyer.getUserWallet(), currencies);
//
//        Stock stock = null;
//        if (existingStockP != null) {
//            // L'acheteur possède déjà le produit, augmentez la quantité.
//            existingStockP.setQuantity(existingStockP.getQuantity() + desiredQuantity);
//
//            // Mettez à jour le solde de l'acheteur et du vendeur.
//            buyer.getUserWallet().setBalance(buyer.getUserWallet().getBalance() - totalPrice);
//            sellerWallet.setBalance(sellerWallet.getBalance() + totalPrice);
//
//            // Décrémentez la quantité mise en vente par le vendeur.
//            market.setQuantity(availableQuantity - desiredQuantity);
//
//            // Enregistrez les modifications de l'objet Stock existant dans la base de données.
//            stockRepository.save(existingStockP);
//        } else if
//        (existingStockD != null) {
//            // L'acheteur possède déjà le produit, augmentez la quantité.
//            existingStockD.setQuantity(existingStockD.getQuantity() + desiredQuantity);
//
//            // Mettez à jour le solde de l'acheteur et du vendeur.
//            buyer.getUserWallet().setBalance(buyer.getUserWallet().getBalance() - totalPrice);
//            sellerWallet.setBalance(sellerWallet.getBalance() + totalPrice);
//
//            // Décrémentez la quantité mise en vente par le vendeur.
//            market.setQuantity(availableQuantity - desiredQuantity);
//
//            // Enregistrez les modifications de l'objet Stock existant dans la base de données.
//            stockRepository.save(existingStockP);
//        } else {
//            // L'acheteur n'a pas encore ce produit, créez un nouvel objet Stock.
//            stock = new Stock();
//            stock.setBuyerWallet(buyer.getUserWallet());
//            stock.setProduct(product);
//            stock.setCurrencies(currencies);
//            stock.setQuantity(desiredQuantity);
//            stock.setPrice(totalPrice);
//            Date dateActuelle = new Date();
//            stock.setDateOfPurchase(dateActuelle);
//
//            // Mettez à jour la quantité mise en vente par le vendeur.
//            market.setQuantity(availableQuantity - desiredQuantity);
//
//            // Enregistrez le nouvel objet Stock dans la base de données.
//            stockRepository.save(stock);
//        }
//
//        // Enregistrez une transaction entre l'acheteur et le vendeur.
//        Transaction transaction = new Transaction();
//        transaction.setWalletBuyer(buyer.getUserWallet());
//        transaction.setWalletSeller(sellerWallet);
//        transaction.setTotalPrice(totalPrice);
//        transaction.setTransactionDate(new Date());
//
//        // Enregistrez la transaction dans la base de données.
//        transactionRepository.save(transaction);
//
//        // Mettez à jour l'état de l'article sur le marché.
//        if (availableQuantity - desiredQuantity == 0) {
//            market.setStatus(SellStatus.Sold);
//        }
//        marketPlaceRepository.save(market);
//
//        return stock;
//    }


//    @Transactional
//    public Stock performPurchasetest(Authentication authentication, Long idMarket, Integer desiredQuantity) {
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        String username = userDetails.getUsername();
//        User buyer = userService.retrieveUserByUsername(username);
//
//        if (buyer == null) {
//            // L'acheteur n'a pas été trouvé
//            return null;
//        }
//
//        MarketPlace market = marketPlaceRepository.findById(idMarket).orElse(null);
//
//        if (market == null || market.getStatus() != SellStatus.ForSale) {
//            // Le marché n'a pas été trouvé ou l'article n'est pas disponible
//            return null;
//        }
//
//        Wallet sellerWallet = market.getSellerWallet();
//
//        if (sellerWallet == null) {
//            // Le portefeuille du vendeur n'a pas été trouvé
//            return null;
//        }
//
//        Product product = market.getProduct();
//        Currencies currencies = market.getCurrencies();
//        int availableQuantity = market.getQuantity();
//        double unitPrice = market.getUnitPrice();
//        double totalPrice = unitPrice * desiredQuantity;
//
//        if (desiredQuantity > availableQuantity) {
//            // La quantité souhaitée est supérieure à la quantité disponible.
//            return null;
//        }
//
//        // Vérifiez si l'acheteur possède déjà ce produit.
//        Stock existingStock = null;
//
//        if (product != null) {
//            existingStock = stockRepository.findByBuyerWalletAndProduct(buyer.getUserWallet(), product);
//        } else if (currencies != null) {
//            existingStock = stockRepository.findByBuyerWalletAndCurrencies(buyer.getUserWallet(), currencies);
//        }
//
//        Stock stock = null;
//        if (existingStock != null) {
//            // L'acheteur possède déjà le produit, augmentez la quantité et le prix.
//            existingStock.setQuantity(existingStock.getQuantity() + desiredQuantity);
//            existingStock.setPrice(existingStock.getPrice() + totalPrice);
//
//            // Mettez à jour le solde de l'acheteur et du vendeur.
//            buyer.getUserWallet().setBalance(buyer.getUserWallet().getBalance() - totalPrice);
//            sellerWallet.setBalance(sellerWallet.getBalance() + totalPrice);
//
//            // Décrémentez la quantité mise en vente par le vendeur.
//            market.setQuantity(availableQuantity - desiredQuantity);
//
//            // Enregistrez les modifications de l'objet Stock existant dans la base de données.
//            stockRepository.save(existingStock);
//        } else {
//            // L'acheteur n'a pas encore ce produit, créez un nouvel objet Stock.
//            stock = new Stock();
//            stock.setBuyerWallet(buyer.getUserWallet());
//            stock.setProduct(product);
//            stock.setCurrencies(currencies);
//            stock.setQuantity(desiredQuantity);
//            stock.setPrice(totalPrice);
//            Date dateActuelle = new Date();
//            stock.setDateOfPurchase(dateActuelle);
//
//            // Mettez à jour la quantité mise en vente par le vendeur.
//            market.setQuantity(availableQuantity - desiredQuantity);
//
//            // Enregistrez le nouvel objet Stock dans la base de données.
//            stockRepository.save(stock);
//        }
//
//        // Enregistrez une transaction entre l'acheteur et le vendeur.
//        Transaction transaction = new Transaction();
//        transaction.setWalletBuyer(buyer.getUserWallet());
//        transaction.setWalletSeller(sellerWallet);
//        transaction.setTotalPrice(totalPrice);
//        transaction.setTransactionDate(new Date());
//
//        // Enregistrez la transaction dans la base de données.
//        transactionRepository.save(transaction);
//
//        // Mettez à jour l'état de l'article sur le marché.
//        if (availableQuantity - desiredQuantity == 0) {
//            market.setStatus(SellStatus.Sold);
//        }
//        marketPlaceRepository.save(market);
//
//        return existingStock != null ? existingStock : stock;
//    }
//
//}

//    public Stock performPurchaseT(Authentication authentication, Long idMarket, Integer desiredQuantity) {
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        User buyer = userService.retrieveUserByUsername(userDetails.getUsername());
//
//        // Vérifier si l'acheteur existe
//        if (buyer == null) {
//            throw new IllegalArgumentException("Acheteur non trouvé");
//        }
//
//        // Récupérer le marché ou générer une exception
//        Optional<MarketPlace> marketOptional = marketPlaceRepository.findById(idMarket);
//        MarketPlace market = marketOptional.orElseThrow(() -> new IllegalArgumentException("Marché non trouvé"));
//
//        // Vérifier si le produit est disponible à la vente
//        if (market.getStatus() != SellStatus.ForSale) {
//            throw new IllegalArgumentException("Le produit n'est pas disponible à la vente");
//        }
//
//        // Vérifier et obtenir le portefeuille du vendeur
//        Wallet sellerWallet = Optional.ofNullable(market.getSellerWallet())
//                .orElseThrow(() -> new IllegalArgumentException("Portefeuille du vendeur non trouvé"));
//
//        Product product = market.getProduct();
//        Currencies currencies = market.getCurrencies();
//
//        // Vérifier si le produit ou les devises sont disponibles
//        if (product == null && currencies == null) {
//            throw new IllegalArgumentException("Produit et devises non disponibles");
//        }
//
//        int availableQuantity = market.getQuantity();
//        double unitPrice = market.getUnitPrice();
//        double totalPrice = unitPrice * desiredQuantity;
//
//        // Vérifier si la quantité souhaitée est disponible
//        if (desiredQuantity > availableQuantity) {
//            throw new IllegalArgumentException("Quantité souhaitée supérieure à la quantité disponible");
//        }
//
//        Wallet buyerWallet = buyer.getUserWallet();
//
//        // Vérifier si l'acheteur a un solde suffisant
//        if (buyerWallet.getBalance() < totalPrice) {
//            throw new IllegalArgumentException("Solde insuffisant");
//        }
//
//        // Obtenir le produit ou les devises
//        if (product != null) {
//            product = rawMaterialsRepository.findById(product.getIdProduct()).orElse(null);
//        } else if (currencies != null) {
//            currencies = currenciesRepository.findById(currencies.getIdCurrency()).orElse(null);
//        }
//
//        // Obtenir un stock existant s'il y en a un
//        Stock existingStock = product != null
//                ? stockRepository.findByBuyerWalletAndProduct(buyerWallet, product)
//                : stockRepository.findByBuyerWalletAndCurrencies(buyerWallet, currencies);
//
//        // Mettre à jour le stock
//        if (existingStock == null) {
//            existingStock = new Stock();
//            existingStock.setBuyerWallet(buyerWallet);
//            existingStock.setProduct(product);
//            existingStock.setCurrencies(currencies);
//            existingStock.setQuantity(desiredQuantity);
//            existingStock.setPrice(totalPrice);
//            existingStock.setDateOfPurchase(new Date());
//        } else {
//            existingStock.setQuantity(existingStock.getQuantity() + desiredQuantity);
//            existingStock.setPrice(existingStock.getPrice() + totalPrice);
//        }
//
//        // Mettre à jour les soldes des portefeuilles
//        buyerWallet.setBalance(buyerWallet.getBalance() - totalPrice);
//        sellerWallet.setBalance(sellerWallet.getBalance() + totalPrice);
//
//        // Mettre à jour la quantité sur le marché
//        market.setQuantity(availableQuantity - desiredQuantity);
//        if (market.getQuantity() == 0) {
//            market.setStatus(SellStatus.Sold);
//        }
//
//        // Enregistrer les modifications dans la base de données
//        stockRepository.save(existingStock);
//        walletRepository.save(buyerWallet);
//        walletRepository.save(sellerWallet);
//        marketPlaceRepository.save(market);
//
//        // Enregistrer la transaction
//        Transaction transaction = new Transaction();
//        transaction.setWalletBuyer(buyerWallet);
//        transaction.setWalletSeller(sellerWallet);
//        transaction.setTotalPrice(totalPrice);
//        transaction.setTransactionDate(new Date());
//        transactionRepository.save(transaction);
//
//        return existingStock;
//    }

//    @Transactional
//    public Stock performPurchaseT(Authentication authentication, Long idMarket, Integer desiredQuantity) {
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        User buyer = userService.retrieveUserByUsername(userDetails.getUsername());
//        System.out.println(buyer);
//        // Vérifier si l'acheteur existe
//        if (buyer == null) {
//            throw new IllegalArgumentException("Acheteur non trouvé");
//        }
//
//        // Récupérer le marché ou générer une exception
//        MarketPlace market = marketPlaceRepository.findById(idMarket)
//                .orElseThrow(() -> new IllegalArgumentException("Marché non trouvé"));
//        System.out.println(market);
//        // Vérifier si le produit est disponible à la vente
//        if (market == null || market.getStatus() != SellStatus.ForSale) {
//            throw new IllegalArgumentException("Marché non trouvé ou l'article n'est pas disponible");
//        }
//        System.out.println("lllll");
//
//        // Vérifier et obtenir le portefeuille du vendeur
//        Wallet sellerWallet = Optional.ofNullable(market.getSellerWallet())
//                .orElseThrow(() -> new IllegalArgumentException("Portefeuille du vendeur non trouvé"));
//
//        Product product = market.getProduct();
//        Currencies currencies = market.getCurrencies();
//
//        // Vérifier si le produit ou les devises sont disponibles
//        if (product == null && currencies == null) {
//            throw new IllegalArgumentException("Produit et devises non disponibles");
//        }
//
//        int availableQuantity = market.getQuantity();
//        double unitPrice = market.getUnitPrice();
//        double totalPrice = unitPrice * desiredQuantity;
//
//        // Vérifier si la quantité souhaitée est disponible
//        if (desiredQuantity > availableQuantity) {
//            throw new IllegalArgumentException("Quantité souhaitée supérieure à la quantité disponible");
//        }
//
//        Wallet buyerWallet = buyer.getUserWallet();
//        System.out.println(buyerWallet);
//        // Vérifier si l'acheteur a un solde suffisant
//        if (buyerWallet.getBalance() < totalPrice) {
//            throw new IllegalArgumentException("Solde insuffisant");
//        }
//
//        // Obtenir le produit ou les devises
//        if (product != null) {
//            product = rawMaterialsRepository.findById(product.getIdProduct()).orElse(null);
//        } else if (currencies != null) {
//            currencies = currenciesRepository.findById(currencies.getIdCurrency()).orElse(null);
//        }
//
//        // Obtenir un stock existant s'il y en a un
//        Stock existingStock = product != null
//                ? stockRepository.findByBuyerWalletAndProduct(buyerWallet, product)
//                : stockRepository.findByBuyerWalletAndCurrencies(buyerWallet, currencies);
//
//        // Mettre à jour le stock
//        if (existingStock == null) {
//            existingStock = new Stock();
//            existingStock.setBuyerWallet(buyerWallet);
//            existingStock.setProduct(product);
//            existingStock.setCurrencies(currencies);
//            existingStock.setQuantity(desiredQuantity);
//            existingStock.setPrice(totalPrice);
//            existingStock.setDateOfPurchase(new Date());
//        } else {
//            existingStock.setQuantity(existingStock.getQuantity() + desiredQuantity);
//            existingStock.setPrice(existingStock.getPrice() + totalPrice);
//        }
//
//        // Mettre à jour les soldes des portefeuilles
//        buyerWallet.setBalance(buyerWallet.getBalance() - totalPrice);
//        sellerWallet.setBalance(sellerWallet.getBalance() + totalPrice);
//
//        // Mettre à jour la quantité sur le marché
//        market.setQuantity(availableQuantity - desiredQuantity);
//        if (market.getQuantity() == 0) {
//            market.setStatus(SellStatus.Sold);
//        }
//
//        // Enregistrer les modifications dans la base de données
//        stockRepository.save(existingStock);
//        walletRepository.save(buyerWallet);
//        walletRepository.save(sellerWallet);
//        marketPlaceRepository.save(market);
//
//        // Enregistrer la transaction
//        Transaction transaction = new Transaction();
//        transaction.setWalletBuyer(buyerWallet);
//        transaction.setWalletSeller(sellerWallet);
//        transaction.setTotalPrice(totalPrice);
//        transaction.setTransactionDate(new Date());
//        transactionRepository.save(transaction);
//
//        return existingStock;
//    }

















