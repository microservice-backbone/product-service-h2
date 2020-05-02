package com.backbone.core.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@Slf4j
public class ProductController {

    @Autowired
    ProductRepository repository;

//  L2 REST   separate GET/POST/DELETE/PUT requests @GetMapping ..
//  L3 REST   HATEOS (return also next actions
//  /product/1
    @GetMapping("/products/{id}")
    public Product getProduct(@PathVariable String id) {
        log.info("Searched product id: {}", id);

        return repository.findById(Integer.parseInt(id));
    }

    @GetMapping("/products")
    public List<Product> getProducts() {
        log.info("Searched all products");

        return repository.findAll();
    }

    @PutMapping("/products")
    public ResponseEntity<Void> addProduct(@RequestBody Product product) {
        log.info("Create request for product: {}", product);

        if (product == null)
            return ResponseEntity.noContent().build();

        int id = repository.save(product).getId();

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
        log.info("Return location after create: {}", location);
        return ResponseEntity.created(location).build();
    }

    @PostMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable String id, @RequestBody Product product) {
        log.info("Update request for product: {}", product);

        if (product == null)
            return ResponseEntity.noContent().build();

        repository.save(product);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand(product.getId()).toUri();
        log.info("Return location after update: {}", location);

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/products/category/{category}")
    public List<Product> getProductsByCategory(@PathVariable String category) {
        log.info("Searched by category : {}", category);

        return repository.findProductsByCategory(category);
    }
}
