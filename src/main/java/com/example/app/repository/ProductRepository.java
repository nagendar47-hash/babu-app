package com.example.app.repository;

import com.example.app.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find by name (case-insensitive)
    List<Product> findByNameContainingIgnoreCase(String name);

    // Find products with stock greater than 0
    List<Product> findByStockGreaterThan(Integer stock);

    // Find products below a price
    @Query("SELECT p FROM Product p WHERE p.price <= :maxPrice ORDER BY p.price ASC")
    List<Product> findByMaxPrice(Double maxPrice);
}
