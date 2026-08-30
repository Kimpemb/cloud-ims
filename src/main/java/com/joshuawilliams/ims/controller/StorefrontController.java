package com.joshuawilliams.ims.controller;

import com.joshuawilliams.ims.dto.order.OrderResponse;
import com.joshuawilliams.ims.dto.product.ProductResponse;
import com.joshuawilliams.ims.dto.store.PlaceOrderRequest;
import com.joshuawilliams.ims.dto.store.SellerProfileResponse;
import com.joshuawilliams.ims.service.StorefrontService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/store")
public class StorefrontController {

    private final StorefrontService storefrontService;

    public StorefrontController(StorefrontService storefrontService) {
        this.storefrontService = storefrontService;
    }

    @GetMapping("/{username}")
    public SellerProfileResponse profile(@PathVariable String username) {
        return storefrontService.getSellerProfile(username);
    }

    @GetMapping("/{username}/products")
    public List<ProductResponse> products(@PathVariable String username,
                                           @RequestParam(required = false) String search) {
        return storefrontService.getAvailableProducts(username, search);
    }

    @GetMapping("/{username}/products/{productId}")
    public ProductResponse productDetail(@PathVariable String username, @PathVariable Long productId) {
        return storefrontService.getAvailableProductDetail(username, productId);
    }

    @PostMapping("/{username}/orders")
    public ResponseEntity<OrderResponse> placeOrder(@PathVariable String username,
                                                      @Valid @RequestBody PlaceOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(storefrontService.placeOrder(username, request));
    }
}
