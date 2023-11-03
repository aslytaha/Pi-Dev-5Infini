package com.example.pidev.Services;

import com.example.pidev.Entities.MarketPlace;
import com.example.pidev.Entities.SellStatus;
import com.example.pidev.Entities.Transaction;
import com.example.pidev.Entities.User;
import com.example.pidev.Interfaces.ITransactionService;
import com.example.pidev.Repositories.TransactionRepository;
import com.example.pidev.Repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.jws.soap.SOAPBinding;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class TransactionService implements ITransactionService {

    @Autowired
    UserService userService;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    UserRepository userRepository;

    @Override
    public List<Transaction> getPurchases(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User currentUser = userService.retrieveUserByUsername(username);

        List<Transaction> transactions = transactionRepository.findAll();
        List<Transaction> purchases = transactions.stream()
                .filter(transaction -> currentUser.getUserWallet().equals(transaction.getWalletBuyer()))
                .collect(Collectors.toList());
        return purchases;
    }

    @Override
    public List<Transaction> getSales(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        User currentUser = userService.retrieveUserByUsername(username);

        List<Transaction> transactions = transactionRepository.findAll();
        List<Transaction> sales = transactions.stream()
                .filter(transaction -> currentUser.getUserWallet().equals(transaction.getWalletSeller()))
                .collect(Collectors.toList());
        return sales;
    }

    @Override
    public List<Transaction> getUserTransactions(Long idUser) {
        User user = userRepository.findById(idUser).orElse(null);

        if (user == null) {
            // Gérer le cas où l'utilisateur n'est pas trouvé, par exemple, en lançant une exception.
            throw new RuntimeException("Utilisateur non trouvé avec l'ID : " + idUser);
        }

        List<Transaction> transactions = transactionRepository.findAll();

        List<Transaction> userTransactions = transactions.stream()
                .filter(transaction -> user.getUserWallet().equals(transaction.getWalletSeller()) || user.getUserWallet().equals(transaction.getWalletBuyer()))
                .collect(Collectors.toList());

        return userTransactions;
    }
}