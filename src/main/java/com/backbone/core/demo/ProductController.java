package com.backbone.core.demo;

import com.backbone.core.demo.service.ProductService;
import com.backbone.core.demo.service.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class ProductController {

    private static final String PAGE = "0";
    private static final String SIZE = "10";

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductService productService;

    @Autowired
    ReviewService reviewService;

//  Read ops

    //todo: enhance log-message format => sometimes we may return other than object like count of records...

    /**
     * Get product by Id. Then call other services to gather more data.
     * It caches data after 1st call w/ id.
     *
     * @cached: product
     *
     * log format: message [param1, param2] : returned-object
     *     message can be => Not found, Get, Returned, Exception
     *
     * @param id Product's Id in URL
     * @return If find, returns Product, and HttpStatus.OK
     *         If parameter is not valid (such as string instead int), returns Null, and HttpStatus.BAD_REQUEST
     *         If not found (empty Product), returns Null, and HttpStatus.NOT_FOUND
     *         If any exception occurs, returns null, and HttpStatus.EXPECTATION_FAILED
     */
    @GetMapping("/product/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable String id) {
        log.info("Get [id:{}]", id);

        try {
            AtomicReference<ResponseEntity<Product>> result = new AtomicReference<>();

            productService.getProduct(id)
                          .ifPresent(product -> {

                    // todo: creates low degree of coupling !
                    // call review-service
                    reviewService.productsReviews(id)
                                 .ifPresent(reviews -> {

                                     log.info("Call review-service [id:{}, count:{}]", id, reviews.size());

                                     product.setReviews(reviews);
                                 });

                    // call other-services, if necessary

                    log.info("Returned [id:{}] : {}", id, product);

                    result.set(new ResponseEntity<>(product, HttpStatus.OK));
            });

            return result.get();

        } catch (NumberFormatException nfe) {
            log.error("Bad request [id:{}] : {}", id, nfe.getMessage());

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (NullPointerException npe) {
            log.warn("Not found [id:{}] : {}", id, npe.getMessage());

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Exception [id:{}] : {}", id, e.getMessage());

            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

//    /**
//     * Get product by Id w/ HATEOS support (GET and DELETE links)
//     *
//     * @param id Product's Id in URL
//     * @return If find, returns Product, and get link, and delete link
//     *         If not found, returns Null
//     *         If any exception occurs, returns null
//     */
//    @GetMapping("v2/product/{id}")
//    public EntityModel<Product> getProductAsHATEOS(@PathVariable String id) {
//        log.info("Get [id:{}]", id);
//
//        try {
//            Optional<Product> product = productService.getProduct(id);
//
//            if (product.isEmpty()) {
//                log.error("Not found [id:{}]", id);
//
//                return new EntityModel<>(null, null, null);
//            }
//
//            Link getLink = WebMvcLinkBuilder.linkTo(ProductController.class)
//                    .slash(product.get().getId())
//                    .withSelfRel();
//
//            Link deleteLink = WebMvcLinkBuilder.
//                    linkTo(WebMvcLinkBuilder
//                            .methodOn(ProductController.class)
//                            .deleteProduct(String.valueOf(product.get().getId())))
//                    .withRel("delete");
//
//            log.info("Returned [id:{}]: {}", id, product.get());
//
//            return new EntityModel<>(product.get(), getLink, deleteLink);
//        } catch (NumberFormatException nfe) {
//            log.error("Bad request [id:{}] : {}", id, nfe.getMessage());
//
//            return new EntityModel<>(null, null, null);
//        } catch (Exception e) {
//            log.error("Exception [id:{}] : {}", id, e.getMessage());
//
//            return new EntityModel<>(null, null, null);
//        }
//    }

    /**
     * Get all products via paging and size.
     * It caches data after 1st call.
     *
     * @cached: product
     *
     * @param page default=0 to ...N
     * @param size default=10, if empty
     * @return If OK, returns List<Product>, and HttpStatus.OK
     *         If parameter is not valid (such as string instead int), returns Null, and HttpStatus.BAD_REQUEST
     *         If page or size have no record, returns null, and HttpStatus.NO_CONTENT
     *         If any exception occurs, returns null, and HttpStatus.EXPECTATION_FAILED
     */
    @GetMapping(path = {"/products",
                        "/products/page/{page}",
                        "/products/page/{page}/size/{size}"})
    public ResponseEntity<List<Product>> getProducts(@PathVariable(required = false) String page,
                                                     @PathVariable(required = false) String size) {
        //set defaults
        String p = Optional.ofNullable(page).orElse(PAGE);
        String s = Optional.ofNullable(size).orElse(SIZE);

        log.info("Get [page:{}, size:{}]", p, s);

        try {
            AtomicReference<ResponseEntity<List<Product>>> result = new AtomicReference<>();

            productService.getProducts(p, s)
                          .ifPresent(products -> {

                            log.info("Returned [page:{}, size:{}] : {}", p, s, products.getContent());

                            result.set(new ResponseEntity<>(products.getContent(), HttpStatus.OK));

                          });

            return result.get();

        } catch (NumberFormatException nfe) {
            log.error("Bad request [page:{}, size:{}] : {}", p, s, nfe.getMessage());

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (NullPointerException npe) {
            log.warn("No content [page:{}, size:{}] : {}", p, s, npe.getMessage());

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            log.error("Exception [page:{}, size:{}] : {}", p, s, e.getMessage());

            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    /**
     * Get all distinct categories
     *
     * @cached: categories
     *
     * @return If OK, returns categories, and HttpStatus.OK
     *         If no record, returns null, and HttpStatus.NOT_FOUND
     *         If any exception occurs, returns null, and HttpStatus.EXPECTATION_FAILED
     */
    @GetMapping("/products/category")
    public ResponseEntity<List<String>> getCategories() {
        log.info("Get");

        try {
            AtomicReference<ResponseEntity<List<String>>> result = new AtomicReference<>();

            productService.getCategories()
                          .ifPresent(categories -> {

                              log.info("Returned : {}", categories);

                              result.set(new ResponseEntity<>(categories, HttpStatus.OK));

                          });

            return result.get();

        } catch (NullPointerException npe) {
            log.warn("Not found : {}", npe.getMessage());

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Exception : {}", e.getMessage());

            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

//  Service ops



//  CreateUpdateDelete ops

    //TODO: in elasticsearch, PUT is arranged as partial product update, POST is creating a new one.

    /**
     * Save product. If,
     *   id == 0, creates
     *   id != 0 and isValid, updates
     *   id != 0 and notValid, creates and risks cache!
     *
     * @param product as JSON in RequestBody
     * @return If OK, returns updated or created Product, and HttpStatus.OK
     *         If RequestBody or Id not OK, returns Null, and HttpStatus.BAD_REQUEST
     *         If any exception occurs, returns null, and HttpStatus.EXPECTATION_FAILED
     */
    @PostMapping("/product")
    public ResponseEntity<Product> saveProduct(@RequestBody Product product) {

        log.info("Save [product:{}]", product);


        try {
            AtomicReference<ResponseEntity<Product>> result = new AtomicReference<>();

            Optional.of(product)
                    .flatMap(product1 ->
                            productService.saveProduct(String.valueOf(product1.getId()), product1))
                                          .ifPresent(updatedProduct -> {
                                              log.info("Saved : {}", updatedProduct);

                                              result.set(new ResponseEntity<>(updatedProduct, HttpStatus.OK));
                                          });

            return result.get();

        } catch (IllegalArgumentException iae) {
            log.error("Bad request [product:{}]", product);

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {

            log.error("Exception [product:{}] : {}", product, e.getMessage());

            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }


    /**
     * Delete product.
     *
     * @cached: product
     *
     * @param id of Product in URL
     * @return If OK, returns HttpStatus.OK
     *         If Input is not OK, returns HttpStatus.BAD_REQUEST
     *         If any exception occurs, returns HttpStatus.EXPECTATION_FAILED
     */
    @DeleteMapping("/product/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        log.info("Delete [id:{}]", id);

        AtomicReference<ResponseEntity<Void>> result = new AtomicReference<>();

        try {

            Optional.of(id)
                    .ifPresent(id1 -> {
                        productService.deleteProduct(id1);

                        log.info("Deleted [id:{}]", id1);

                        result.set(new ResponseEntity<>(HttpStatus.OK));
                    });

            return result.get();

        } catch (IllegalArgumentException iae) {
            log.error("Bad request [id:{}]", id);

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            log.error("Exception [id:{}] : {}", id, e.getMessage());

            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

//  Search ops

    /**
     * Get products by category via paging and size.
     *
     * @cached: nope
     *
     * @param category category of product
     * @param page default=0 to ...N
     * @param size default=10, if empty
     * @return If OK, returns List<Product>, and HttpStatus.OK
     *         If page or size have no record, returns null, and HttpStatus.NO_CONTENT
     *         If any exception occurs, returns null, and HttpStatus.EXPECTATION_FAILED
     */
    @GetMapping(path = {"/products/category/{category}",
                        "/products/category/{category}/page/{page}",
                        "/products/category/{category}/page/{page}/size/{size}"})
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category,
                                                               @PathVariable(required = false) String page,
                                                               @PathVariable(required = false) String size) {
        // set defaults
        String p = Optional.ofNullable(page).orElse(PAGE);
        String s = Optional.ofNullable(size).orElse(SIZE);

        log.info("Get [category:{}, page:{}, size:{}]", category, page, size);

        try {
            AtomicReference<ResponseEntity<List<Product>>> result = new AtomicReference<>();

            productService.getProductsByCategory(category, p, s)
                          .ifPresent(products -> {

                              //todo: returning all reviews in logs vs part of it. better way (debug level) may be good
                              log.info("Returned [category:{}, page:{}, size:{}] : {}", category
                                                                                        , p
                                                                                        , s
                                                                                        , products.getContent());

                              result.set(new ResponseEntity<>(products.getContent(), HttpStatus.OK));
                          });

            return result.get();

        } catch (NullPointerException npe) {
            log.warn("No content [category:{}, page:{}, size:{}]", category, p, s);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            log.error("Exception [category:{}, page:{}, size:{}] : {}", category, page, size, e.getMessage());

            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

}
