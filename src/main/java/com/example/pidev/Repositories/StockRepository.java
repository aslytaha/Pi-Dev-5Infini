package com.example.pidev.Repositories;

import com.example.pidev.Entities.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StockRepository extends JpaRepository<Stock,Long> {


//    Stock NewPurchase(Long idProduct ,Long idCurrency, )
}
