package com.example.pidev.Repositories;

import com.example.pidev.Entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawMaterialsRepository extends JpaRepository<Product,Long> {
}
