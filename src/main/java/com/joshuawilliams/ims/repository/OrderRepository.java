package com.joshuawilliams.ims.repository;

import com.joshuawilliams.ims.entity.Order;
import com.joshuawilliams.ims.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // seller dashboard - all orders placed against this seller's products
    List<Order> findBySeller(User seller);

    // scoped lookup so one seller can't fetch/update another seller's order
    Optional<Order> findByIdAndSeller(Long id, User seller);
}
