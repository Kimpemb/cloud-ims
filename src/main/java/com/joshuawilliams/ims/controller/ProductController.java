package com.joshuawilliams.ims.controller;

import com.joshuawilliams.ims.dto.product.ProductRequest;
import com.joshuawilliams.ims.dto.product.ProductResponse;
import com.joshuawilliams.ims.security.SecurityUserDetails;
import com.joshuawilliams.ims.service.ProductService;
import com.joshuawilliams.ims.service.S3StorageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final S3StorageService s3StorageService;

    public ProductController(ProductService productService, S3StorageService s3StorageService) {
        this.productService = productService;
        this.s3StorageService = s3StorageService;
    }

    @GetMapping
    public List<ProductResponse> list(@AuthenticationPrincipal SecurityUserDetails principal) {
        return productService.getSellerProducts(principal.getUser());
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@AuthenticationPrincipal SecurityUserDetails principal,
                                                    @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(principal.getUser(), request));
    }

    @GetMapping("/{id}")
    public ProductResponse get(@AuthenticationPrincipal SecurityUserDetails principal, @PathVariable Long id) {
        return productService.getSellerProduct(principal.getUser(), id);
    }

    @PutMapping("/{id}")
    public ProductResponse update(@AuthenticationPrincipal SecurityUserDetails principal,
                                   @PathVariable Long id,
                                   @Valid @RequestBody ProductRequest request) {
        return productService.updateProduct(principal.getUser(), id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal SecurityUserDetails principal, @PathVariable Long id) {
        productService.deleteProduct(principal.getUser(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/image")
    public ProductResponse uploadImage(@AuthenticationPrincipal SecurityUserDetails principal,
                                        @PathVariable Long id,
                                        @RequestParam("file") MultipartFile file) {
        String imageUrl = s3StorageService.uploadProductImage(id, file);
        return productService.updateProductImage(principal.getUser(), id, imageUrl);
    }
}
