package com.example.pidev.Repositories;

import com.example.pidev.Entities.MarketPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketPlaceRepository extends JpaRepository<MarketPlace,Long> {
}
