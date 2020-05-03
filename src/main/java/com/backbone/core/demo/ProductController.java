package com.backbone.core.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class ProductController {

    @Autowired
    ProductRepository repository;

//  L2 REST   separate GET/POST/DELETE/PUT requests @GetMapping ..
//  L3 REST   HATEOS (return also next actions
//  /product/1

    /**
     * Get product by Id
     *
     * @param id Product's Id in URL
     * @return If find, returns Product, and HttpStatus.OK
     *         If not found, returns Null, and HttpStatus.NOT_FOUND
     *         If any exception occurs, returns null, and HttpStatus.EXPECTATION_FAILED
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable String id) {
        log.info("Get product by id: {}", id);

        try {
            Optional<Product> product = repository.findById(Integer.parseInt(id));

            if (product.isEmpty()) {
                log.warn("Get product by id {}: {}", id, "Product not found");

                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            log.info("Returned product: {}", product.get());

            return new ResponseEntity<>(product.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Get product by id {}: {}", id, e.getMessage());

            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    /**
     * Get all products. Just for demonstration purposes.
     *
     * @return If OK, returns List<Product>, and HttpStatus.OK
     *         If any exception occurs, returns null, and HttpStatus.EXPECTATION_FAILED
     */
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getProducts() {
        log.info("Get products");

        try {
            Optional<List<Product>> products = Optional.of(repository.findAll());

            // todo: if returns no value, means error

            log.info("Returned 3 of.. products: {}", products.get().subList(0,3));

            return new ResponseEntity<>(products.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Get products: {}", e.getMessage());

            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    /**
     * Create product.
     *
     * @param product as JSON in RequestBody
     * @return If OK, returns Product, new Id in Headers, and HttpStatus.OK
     *         If RequestBody not OK, returns Null, and HttpStatus.BAD_REQUEST
     *         If any exception occurs, returns null, and HttpStatus.EXPECTATION_FAILED
     */
    @PostMapping("/products")
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        log.info("Create product: {}", product);

        if (product == null) {
            log.error("Create product: {}", "Product is null");

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            Product createdProduct = repository.save(product);

            HttpHeaders headers= new HttpHeaders();
            headers.add("id", String.valueOf(createdProduct.getId()));

            log.info("Created product: {}", createdProduct);

            return new ResponseEntity<>(createdProduct, headers, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Create product: {}", e.getMessage());

            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    /**
     * Update product.
     *
     * @param id of Product in URL
     * @param product as JSON in RequestBody
     * @return If OK, returns updated Product, and HttpStatus.OK
     *         If RequestBody or Id not OK, returns Null, and HttpStatus.BAD_REQUEST
     *         If any exception occurs, returns null, and HttpStatus.EXPECTATION_FAILED
     */
    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable String id, @RequestBody Product product) {
        log.info("Update product id: {} : {}", id, product);

        if (product == null || id == null) {
            log.error("Update product: {}", "Product or id is null");

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            Product updatedProduct = repository.save(product);

            log.info("Updated product: {}", updatedProduct);

            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Update product id: {} : {}", id, e.getMessage());

            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    /**
     * Get all distinct categories
     * select distinct category from product
     *
     * @return List<String>, and HttpStatus
     * @throws Exception .
     */
    @GetMapping("/products/category")
    public ResponseEntity<List<String>> getCategories() throws Exception {
        log.info("Get categories");

        try {
            Optional<List<String>> categories = repository.getDistinctCategories();

            log.info("Returned categories: {}", categories.get());

            return new ResponseEntity<>(categories.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Get categories: {}", e.getMessage());

            throw e;
        }
    }

    /**
     * Get products by category
     *
     * @param category Category string in URL
     * @return List<Product, and HttpStatus
     * @throws Exception .
     */
    @GetMapping("/products/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) throws Exception {
        log.info("Get products by category : {}", category);

        try {
            Optional<List<Product>> products = repository.findProductsByCategory(category);

            log.info("Returned 3 of.. products by category: {}", products.get().subList(0,3));

            return new ResponseEntity<>(products.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Get products by category: {}", e.getMessage());

            throw e;
        }
    }
}
