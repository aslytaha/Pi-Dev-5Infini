package com.example.pidev.Repositories;

import com.example.pidev.Entities.Currencies;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrenciesRepository extends JpaRepository<Currencies,Long> {
}
