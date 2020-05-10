package com.backbone.core.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Optional<Product> findById(int id);
    Optional<List<Product>> findByCategory(String category);

    /**
     *  if you need paging and sorting, in custom methods
     * Optional<List<Product>> findByCategory(String category, Pageable pageable);
     *
     */

    @Query("select distinct p.category from Product p")
    Optional<List<String>> getDistinctCategories();

    /**
     * JPQL queries, if you need
     *
     * @Query("select p from Product p")
     * List<Product> retrieveAllProducts();
     */

    /**
     * Native (SQL) queries, if you need
     *
     *
     */

//    This class has some default methods already implemented such as save(), findAll()
}
