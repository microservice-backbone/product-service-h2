package com.backbone.core.demo.service;

import com.backbone.core.demo.Review;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Service
@FeignClient(name = "review-service", url = "http://localhost:8084")
public interface ReviewService {

    @GetMapping("reviews/product/{productId}")
    @Cacheable(value = "review", key = "#productId", unless = "#result == null")
    Optional<List<Review>> productsReviews(@PathVariable String productId);

}
