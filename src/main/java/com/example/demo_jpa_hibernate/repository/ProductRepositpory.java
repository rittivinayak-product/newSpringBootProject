package com.example.demo_jpa_hibernate.repository;

import com.example.demo_jpa_hibernate.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepositpory extends JpaRepository<Product, Long> {

    List<Product> findAll();

    @Query("select p from Product p where p.name like %:name%")
    List<Product> searchByName(@Param("name") String name);

    @Query("SELECT p FROM Product p WHERE (:name IS NULL OR LOWER(p.name) LIKE CONCAT('%', :name, '%')) AND (:minPrice IS NULL OR p.price >= :minPrice) AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    List<Product> searchProducts(@Param("name") String name, @Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
}

