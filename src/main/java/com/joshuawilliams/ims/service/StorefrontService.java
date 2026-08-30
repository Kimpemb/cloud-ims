package com.joshuawilliams.ims.service;

import com.joshuawilliams.ims.dto.order.OrderMapper;
import com.joshuawilliams.ims.dto.order.OrderResponse;
import com.joshuawilliams.ims.dto.product.ProductMapper;
import com.joshuawilliams.ims.dto.product.ProductResponse;
import com.joshuawilliams.ims.dto.store.PlaceOrderRequest;
import com.joshuawilliams.ims.dto.store.SellerProfileResponse;
import com.joshuawilliams.ims.entity.Order;
import com.joshuawilliams.ims.entity.OrderStatus;
import com.joshuawilliams.ims.entity.Product;
import com.joshuawilliams.ims.entity.ProductStatus;
import com.joshuawilliams.ims.entity.User;
import com.joshuawilliams.ims.exception.ProductNotAvailableException;
import com.joshuawilliams.ims.exception.ResourceNotFoundException;
import com.joshuawilliams.ims.repository.OrderRepository;
import com.joshuawilliams.ims.repository.ProductRepository;
import com.joshuawilliams.ims.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Public, unauthenticated surface - the buyer-facing side of the platform.
 * Only ever exposes AVAILABLE products; never leaks RESERVED/SOLD inventory
 * or another seller's data.
 */
@Service
public class StorefrontService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public StorefrontService(UserRepository userRepository,
                              ProductRepository productRepository,
                              OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    public SellerProfileResponse getSellerProfile(String username) {
        User seller = findSellerOrThrow(username);
        long available = productRepository.findBySellerAndStatus(seller, ProductStatus.AVAILABLE).size();

        return SellerProfileResponse.builder()
                .username(seller.getUsername())
                .businessName(seller.getBusinessName())
                .availableProductCount(available)
                .build();
    }

    public List<ProductResponse> getAvailableProducts(String username, String search) {
        User seller = findSellerOrThrow(username);
        List<Product> products = productRepository.findBySellerAndStatus(seller, ProductStatus.AVAILABLE);

        if (search != null && !search.isBlank()) {
            String needle = search.toLowerCase();
            products = products.stream()
                    .filter(p -> p.getName().toLowerCase().contains(needle)
                            || (p.getBrand() != null && p.getBrand().toLowerCase().contains(needle)))
                    .toList();
        }

        return products.stream().map(ProductMapper::toResponse).toList();
    }

    public ProductResponse getAvailableProductDetail(String username, Long productId) {
        User seller = findSellerOrThrow(username);
        Product product = productRepository.findByIdAndSeller(productId, seller)
                .filter(p -> p.getStatus() == ProductStatus.AVAILABLE)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        return ProductMapper.toResponse(product);
    }

    /**
     * Core mechanic (MVP.md): flips the product out of AVAILABLE the moment
     * an order is placed, before any payment exists. Transactional so the
     * check-then-reserve can't race between two buyers.
     */
    @Transactional
    public OrderResponse placeOrder(String username, PlaceOrderRequest request) {
        User seller = findSellerOrThrow(username);
        Product product = productRepository.findByIdAndSeller(request.getProductId(), seller)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.getStatus() != ProductStatus.AVAILABLE) {
            throw new ProductNotAvailableException("This item is no longer available");
        }

        product.setStatus(ProductStatus.RESERVED);
        productRepository.save(product);

        Order order = Order.builder()
                .product(product)
                .seller(seller)
                .buyerName(request.getBuyerName())
                .buyerContact(request.getBuyerContact())
                .status(OrderStatus.PENDING)
                .totalAmount(product.getPrice())
                .build();

        return OrderMapper.toResponse(orderRepository.save(order));
    }

    private User findSellerOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found"));
    }
}
