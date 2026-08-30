package com.joshuawilliams.ims.dto.order;

import com.joshuawilliams.ims.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String buyerName;
    private String buyerContact;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private Instant createdAt;
    private Instant updatedAt;
}
