package com.joshuawilliams.ims.service;

import com.joshuawilliams.ims.dto.order.OrderMapper;
import com.joshuawilliams.ims.dto.order.OrderResponse;
import com.joshuawilliams.ims.entity.Order;
import com.joshuawilliams.ims.entity.OrderStatus;
import com.joshuawilliams.ims.entity.Product;
import com.joshuawilliams.ims.entity.ProductStatus;
import com.joshuawilliams.ims.entity.User;
import com.joshuawilliams.ims.exception.InvalidOrderTransitionException;
import com.joshuawilliams.ims.exception.ResourceNotFoundException;
import com.joshuawilliams.ims.repository.OrderRepository;
import com.joshuawilliams.ims.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public List<OrderResponse> getSellerOrders(User seller) {
        return orderRepository.findBySeller(seller).stream()
                .map(OrderMapper::toResponse)
                .toList();
    }

    public OrderResponse getSellerOrder(User seller, Long id) {
        return OrderMapper.toResponse(findOwnedOrder(seller, id));
    }

    /**
     * Enforces the state machine from MVP.md:
     * PENDING -> CONFIRMED | CANCELLED
     * CONFIRMED -> COMPLETED | CANCELLED
     * COMPLETED / CANCELLED are terminal.
     *
     * CANCELLED releases the product back to AVAILABLE.
     * COMPLETED marks the product SOLD.
     */
    @Transactional
    public OrderResponse updateOrderStatus(User seller, Long id, OrderStatus newStatus) {
        Order order = findOwnedOrder(seller, id);
        validateTransition(order.getStatus(), newStatus);

        order.setStatus(newStatus);

        Product product = order.getProduct();
        if (newStatus == OrderStatus.COMPLETED) {
            product.setStatus(ProductStatus.SOLD);
        } else if (newStatus == OrderStatus.CANCELLED) {
            product.setStatus(ProductStatus.AVAILABLE);
        }
        productRepository.save(product);

        return OrderMapper.toResponse(orderRepository.save(order));
    }

    private Order findOwnedOrder(User seller, Long id) {
        return orderRepository.findByIdAndSeller(id, seller)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    private void validateTransition(OrderStatus current, OrderStatus next) {
        boolean valid = switch (current) {
            case PENDING -> next == OrderStatus.CONFIRMED || next == OrderStatus.CANCELLED;
            case CONFIRMED -> next == OrderStatus.COMPLETED || next == OrderStatus.CANCELLED;
            case COMPLETED, CANCELLED -> false;
        };
        if (!valid) {
            throw new InvalidOrderTransitionException(
                    "Cannot move an order from " + current + " to " + next);
        }
    }
}
