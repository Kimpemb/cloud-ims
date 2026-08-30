package com.joshuawilliams.ims.dto.product;

import com.joshuawilliams.ims.entity.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private String brand;
    private String size;
    private String condition;
    private BigDecimal price;
    private String imageUrl;
    private ProductStatus status;
    private Long categoryId;
    private String categoryName;
    private Instant createdAt;
    private Instant updatedAt;
}
