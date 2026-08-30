package com.joshuawilliams.ims.service;

import com.joshuawilliams.ims.dto.product.ProductMapper;
import com.joshuawilliams.ims.dto.product.ProductRequest;
import com.joshuawilliams.ims.dto.product.ProductResponse;
import com.joshuawilliams.ims.entity.Category;
import com.joshuawilliams.ims.entity.Product;
import com.joshuawilliams.ims.entity.ProductStatus;
import com.joshuawilliams.ims.entity.User;
import com.joshuawilliams.ims.exception.ResourceNotFoundException;
import com.joshuawilliams.ims.repository.CategoryRepository;
import com.joshuawilliams.ims.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<ProductResponse> getSellerProducts(User seller) {
        return productRepository.findBySeller(seller).stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    public ProductResponse createProduct(User seller, ProductRequest request) {
        Product product = Product.builder()
                .seller(seller)
                .category(resolveCategory(request.getCategoryId()))
                .name(request.getName())
                .description(request.getDescription())
                .brand(request.getBrand())
                .size(request.getSize())
                .condition(request.getCondition())
                .price(request.getPrice())
                .status(ProductStatus.AVAILABLE)
                .build();

        return ProductMapper.toResponse(productRepository.save(product));
    }

    public ProductResponse getSellerProduct(User seller, Long id) {
        return ProductMapper.toResponse(findOwnedProduct(seller, id));
    }

    public ProductResponse updateProduct(User seller, Long id, ProductRequest request) {
        Product product = findOwnedProduct(seller, id);

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setBrand(request.getBrand());
        product.setSize(request.getSize());
        product.setCondition(request.getCondition());
        product.setPrice(request.getPrice());
        product.setCategory(resolveCategory(request.getCategoryId()));

        return ProductMapper.toResponse(productRepository.save(product));
    }

    public void deleteProduct(User seller, Long id) {
        productRepository.delete(findOwnedProduct(seller, id));
    }

    public ProductResponse updateProductImage(User seller, Long id, String imageUrl) {
        Product product = findOwnedProduct(seller, id);
        product.setImageUrl(imageUrl);
        return ProductMapper.toResponse(productRepository.save(product));
    }

    private Product findOwnedProduct(User seller, Long id) {
        return productRepository.findByIdAndSeller(id, seller)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    private Category resolveCategory(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }
}
