package com.example.pidev.Controllers;

import com.example.pidev.Entities.MarketPlace;
import com.example.pidev.Entities.Transaction;
import com.example.pidev.Services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController

public class TransactionsController {

    @Autowired
    TransactionService transactionService;

    @GetMapping("/Mypurchases")
    public List<Transaction> getMypurchases(Authentication authentication) {
        List<Transaction> transactions = transactionService.getPurchases(authentication);
        return transactions;


    }

    @GetMapping("/MySales")
    public List<Transaction> getMySales(Authentication authentication) {
        List<Transaction> transactions = transactionService.getSales(authentication);
        return transactions;


    }

    @GetMapping("/Transactions/{idUser}")
    public List<Transaction> getUserTransactions(@PathVariable Long idUser) {
        List<Transaction> transactions = transactionService.getUserTransactions(idUser);
        return transactions;
    }


    }

