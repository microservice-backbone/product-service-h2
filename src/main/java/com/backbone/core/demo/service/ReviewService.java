package com.backbone.core.demo.service;

import com.backbone.core.demo.Product;
import com.backbone.core.demo.Review;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
@FeignClient(name = "review-service", url = "http://localhost:8084")
public interface ReviewService {

    @GetMapping("reviews/{productId}/product")
    ResponseEntity<List<Review>> productsReviews(@PathVariable String productId);

}
