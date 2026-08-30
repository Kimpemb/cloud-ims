package com.joshuawilliams.ims.repository;

import com.joshuawilliams.ims.entity.Product;
import com.joshuawilliams.ims.entity.ProductStatus;
import com.joshuawilliams.ims.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findBySeller(User seller);

    List<Product> findBySellerAndStatus(User seller, ProductStatus status);

    Optional<Product> findByIdAndSeller(Long id, User seller);
}
