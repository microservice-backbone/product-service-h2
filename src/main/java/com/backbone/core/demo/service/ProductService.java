package com.backbone.core.demo.service;

import com.backbone.core.demo.Product;
import com.backbone.core.demo.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProductService {

    //todo: If you use PWA on frontend-app, you dont need cache-complexity here !

    @Autowired
    ProductRepository productRepository;

    @Cacheable(value = "product", key = "#id", unless = "#result == null")
    public Optional<Product> getProduct(String id) {

        Optional<Product> product = productRepository.findById(Integer.parseInt(id));

        if (product.isEmpty())
            throw new NullPointerException("No record");

        return product;
    }

    @Cacheable(value = "products", unless = "#result == null")
    public Optional<Page<Product>> getProducts(String page, String size) {

        // todo: returning Optional instead of Page, and handling in ReviewController can be more concise?
        Page<Product> products = productRepository.findAll(PageRequest.of(Integer.parseInt(page),
                Integer.parseInt(size)));

        if (products.isEmpty())
            throw new NullPointerException("No record");

        return Optional.of(products);
    }

    @Cacheable(value = "categories", unless = "#result == null")
    public Optional<List<String>> getCategories() {

        Optional<List<String>> categories = productRepository.getDistinctCategories();

        if (categories.isEmpty())
            throw new NullPointerException("No record");

        return categories;
    }

    @Caching(evict = { @CacheEvict(value = {"products"}),
                       @CacheEvict(value = {"product"}, allEntries = true)})
    public Optional<Product> saveProduct(String id, Product product) {

        Product updatedProduct = productRepository.save(product);

        return Optional.of(updatedProduct);
    }

    @Caching(evict = { @CacheEvict(value = {"product"}, key = "#id"),
                       @CacheEvict(value = {"products"}) })
    public void deleteProduct(String id) {

        productRepository.deleteById(Integer.valueOf(id));
    }

    //todo: check caching, implement a new cache
    //@Cacheable(value = "productsByCategory", unless = "#result == null")
    public Optional<Page<Product>> getProductsByCategory(String category, String page, String size) {

        Page<Product> products = productRepository.findByCategory(category,
                PageRequest.of(Integer.parseInt(page),
                        Integer.parseInt(size)));

        if (products.isEmpty())
            throw new NullPointerException("No record");

        return Optional.of(products);
    }


}
