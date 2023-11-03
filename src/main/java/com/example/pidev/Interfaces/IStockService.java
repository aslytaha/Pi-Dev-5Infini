package com.example.pidev.Interfaces;

import com.example.pidev.Entities.Stock;
import org.springframework.security.core.Authentication;

public interface IStockService {

    Stock newPurchase(Authentication authentication ,Long idProduct ,Long idCurrency, Integer quantity);
}
