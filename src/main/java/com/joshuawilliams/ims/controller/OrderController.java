package com.joshuawilliams.ims.controller;

import com.joshuawilliams.ims.dto.order.OrderResponse;
import com.joshuawilliams.ims.dto.order.OrderStatusUpdateRequest;
import com.joshuawilliams.ims.security.SecurityUserDetails;
import com.joshuawilliams.ims.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<OrderResponse> list(@AuthenticationPrincipal SecurityUserDetails principal) {
        return orderService.getSellerOrders(principal.getUser());
    }

    @GetMapping("/{id}")
    public OrderResponse get(@AuthenticationPrincipal SecurityUserDetails principal, @PathVariable Long id) {
        return orderService.getSellerOrder(principal.getUser(), id);
    }

    @PutMapping("/{id}/status")
    public OrderResponse updateStatus(@AuthenticationPrincipal SecurityUserDetails principal,
                                       @PathVariable Long id,
                                       @Valid @RequestBody OrderStatusUpdateRequest request) {
        return orderService.updateOrderStatus(principal.getUser(), id, request.getStatus());
    }
}
