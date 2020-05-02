package com.backbone.core.demo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Product findById(int id);
    List<Product> findProductsByCategory(String category);

//    for custom queries. normally findAll() is available!
//    @Query("select p from Product p")
//    List<Product> retrieveAllProducts();
}
