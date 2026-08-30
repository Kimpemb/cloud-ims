package com.joshuawilliams.ims.dto.order;

import com.joshuawilliams.ims.entity.Order;

public final class OrderMapper {

    private OrderMapper() {
    }

    public static OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .productId(order.getProduct().getId())
                .productName(order.getProduct().getName())
                .buyerName(order.getBuyerName())
                .buyerContact(order.getBuyerContact())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
