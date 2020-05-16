package com.backbone.core.demo;

import com.backbone.core.demo.service.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class ProductController {

    private static final String PAGE = "0";
    private static final String SIZE = "10";

    @Autowired
    ProductRepository repository;

    @Autowired
    ReviewService reviewService;

//  Read ops

    //todo: enhance log-message format => sometimes we may return other than object like count of records...

    /**
     * Get product by Id. Then call other services to gather more data
     *
     * @returnedData: product (detail)
     *                review
     *                ....
     *
     * log format: message [param1, param2] : returned-object
     *     message can be => Not found, Get, Returned, Exception
     *
     * @param id Product's Id in URL
     * @return If find, returns Product, and HttpStatus.OK
     *         If not found, returns Null, and HttpStatus.NOT_FOUND
     *         If any exception occurs, returns null, and HttpStatus.EXPECTATION_FAILED
     */
    @GetMapping("/product/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable String id) {
        log.info("Get [id:{}]", id);

        try {
            Optional<Product> product = repository.findById(Integer.parseInt(id));

            if (product.isEmpty()) {
                log.warn("Not found [id:{}]", id);

                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            /** todo: if no reviews, handle it better! especially not found, exception, bad request.
             * something is about feign client !
             */
            // call review-service
            ResponseEntity<Optional<List<Review>>> reviews = reviewService.productsReviews(id);

            if (reviews.getBody().isPresent()) {
                if (reviews.hasBody()) {
                    log.info("Call review-service [id:{}, count:{}]", id, reviews.getBody().get().size());

                    product.get().setReviews(reviews.getBody().get());
                }
            }

            // call other-services, where necessary

            log.info("Returned [id:{}] : {}", id, product.get());

            return new ResponseEntity<>(product.get(), HttpStatus.OK);

        } catch (NumberFormatException nfe) {
            log.error("Bad request [id:{}] : {}", id, nfe.getMessage());

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Exception [id:{}] : {}", id, e.getMessage());

            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    /**
     * Get product by Id w/ HATEOS support (GET and DELETE links)
     *
     * @param id Product's Id in URL
     * @return If find, returns Product, and get link, and delete link
     *         If not found, returns Null
     *         If any exception occurs, returns null
     */
    @GetMapping("v2/product/{id}")
    public EntityModel<Product> getProductAsHATEOS(@PathVariable String id) {
        log.info("Get [id:{}]", id);

        try {
            Optional<Product> product = repository.findById(Integer.parseInt(id));

            if (product.isEmpty()) {
                log.error("Not found [id:{}]", id);

                return new EntityModel<>(null, null, null);
            }

            Link getLink = WebMvcLinkBuilder.linkTo(ProductController.class)
                    .slash(product.get().getId())
                    .withSelfRel();

            Link deleteLink = WebMvcLinkBuilder.
                    linkTo(WebMvcLinkBuilder
                            .methodOn(ProductController.class)
                            .deleteProduct(String.valueOf(product.get().getId())))
                    .withRel("delete");

            log.info("Returned [id:{}]: {}", id, product.get());

            return new EntityModel<>(product.get(), getLink, deleteLink);
        } catch (NumberFormatException nfe) {
            log.error("Bad request [id:{}] : {}", id, nfe.getMessage());

            return new EntityModel<>(null, null, null);
        } catch (Exception e) {
            log.error("Exception [id:{}] : {}", id, e.getMessage());

            return new EntityModel<>(null, null, null);
        }
    }

    /**
     * Get all products via paging and size.
     *
     * @returnedData: product (header)
     *
     *
     * @param page default=0 to ...N
     * @param size default=10, if empty
     * @return If OK, returns List<Review>, and HttpStatus.OK
     *         If any exception occurs, returns null, and HttpStatus.EXPECTATION_FAILED
     *         If page or size has no record, returns null, and HttpStatus.NOT_FOUND
     */
    @GetMapping(path = {"/products",
                        "/products/page/{page}",
                        "/products/page/{page}/size/{size}"})
    public ResponseEntity<List<Product>> getProducts(@PathVariable(required = false) String page,
                                                     @PathVariable(required = false) String size) {
        // set defaults
        page = (page == null) ? PAGE : page;
        size = (size == null) ? SIZE : size;

        log.info("Get [page:{}, size:{}]", page, size);

        try {
            Page<Product> products = repository.findAll(PageRequest.of(Integer.parseInt(page),
                                                                       Integer.parseInt(size)));

            // if no records found,
            if (products.isEmpty()) {
                log.error("Not found [page:{}, size:{}]", page, size);

                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            log.info("Returned [page:{}, size:{}] : {}", page,
                                                         size,
                                                         products.get().collect(Collectors.toList()));

            return new ResponseEntity<>(products.get().collect(Collectors.toList()), HttpStatus.OK);
        } catch (NumberFormatException nfe) {
            log.error("Bad request [page:{}, size:{}] : {}", page, size, nfe.getMessage());

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Exception [page:{}, size:{}] : {}", page, size, e.getMessage());

            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    /**
     * Get all distinct categories
     *
     * query: select distinct category from product
     *
     * @return List<String>, and HttpStatus
     * @throws Exception .
     */
    @GetMapping("/products/category")
    public ResponseEntity<List<String>> getCategories() {
        log.info("Get");

        try {
            Optional<List<String>> categories = repository.getDistinctCategories();

            if (categories.isEmpty()) {
                log.error("Not found : {}", (Object) null);

                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            log.info("Returned : {}", categories.get());

            return new ResponseEntity<>(categories.get(), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception : {}", e.getMessage());

            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

//  Service ops



//  CreateUpdateDelete ops

    /**
     * Create product.
     *
     * @param product as JSON in RequestBody
     * @return If OK, creates Product, new Id in Headers, and HttpStatus.CREATED
     *         If RequestBody not OK, returns Null, and HttpStatus.BAD_REQUEST
     *         If any exception occurs, returns null, and HttpStatus.EXPECTATION_FAILED
     */
    @PostMapping("/product")
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        log.info("Create [product:{}]", product);

        if (product == null) {
            log.error("Bad request [review:{}]", (Object) null);

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            Product createdProduct = repository.save(product);

            HttpHeaders headers= new HttpHeaders();
            headers.add("id", String.valueOf(createdProduct.getId()));

            log.info("Created : {}", createdProduct);

            return new ResponseEntity<>(createdProduct, headers, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Exception [product:{}] : {}", product, e.getMessage());

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
    @PutMapping("/product/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable String id, @RequestBody Product product) {
        log.info("Update [id:{}, product:{}]", id, product);

        if (product == null || id == null) {
            log.error("Bad request [id:{}, product:{}]", id, product);

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            Product updatedProduct = repository.save(product);

            log.info("Updated : {}", updatedProduct);

            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception [id:{}] : {}", id, e.getMessage());

            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    /**
     * Delete product.
     *
     * @param id of Product in URL
     * @return If OK, returns HttpStatus.OK
     *         If Id not OK, returns HttpStatus.BAD_REQUEST
     *         If any exception occurs, returns HttpStatus.EXPECTATION_FAILED
     */
    @DeleteMapping("/product/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        log.info("Delete [id:{}]", id);

        if (id == null) {
            log.error("Bad request [id:{}]", (Object) null);

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            repository.deleteById(Integer.valueOf(id));

            log.info("Deleted [id:{}]", id);

            return new ResponseEntity<>(HttpStatus.OK);

        } catch (Exception e) {
            log.error("Exception [id:{}] : {}", id, e.getMessage());

            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

//  Search ops

    /**
     * Get products by category
     *
     * @returnedData: product (header)
     *
     * @param category Category string in URL
     * @return List<Product>, and HttpStatus
     */
    @GetMapping({"/products/category/{category}",
                 "/products/category/{category}/page/{page}",
                 "/products/category/{category}/page/{page}/size/{size}"})
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category,
                                                               @PathVariable(required = false) String page,
                                                               @PathVariable(required = false) String size) {
        // set defaults
        page = (page == null) ? PAGE : page;
        size = (size == null) ? SIZE : size;

        log.info("Get [category:{}, page:{}, size:{}]", category, page, size);

        try {
            Page<Product> products = repository.findByCategory(category,
                                                                         PageRequest.of(Integer.parseInt(page),
                                                                                        Integer.parseInt(size)));

            if (products.isEmpty()) {
                log.error("Not found [category:{}, page:{}, size:{}]", category, page, size);

                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            log.info("Returned [category:{}, page:{}, size:{}] : {}", category,
                                                                      page,
                                                                      size,
                                                                      products.get().collect(Collectors.toList()));

            return new ResponseEntity<>(products.get().collect(Collectors.toList()), HttpStatus.OK);

        } catch (Exception e) {
            log.error("Exception [category:{}, page:{}, size:{}] : {}", category, page, size, e.getMessage());

            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

}
