package com.example.pidev.Interfaces;

import com.example.pidev.Entities.Transaction;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ITransactionService {

    List<Transaction> getPurchases(Authentication authentication);
    List<Transaction> getSales(Authentication authentication);


    List<Transaction> getUserTransactions(Long idUser);
}
