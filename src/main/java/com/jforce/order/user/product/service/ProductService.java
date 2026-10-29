package com.jforce.order.user.product.service;

import com.jforce.order.api.dto.ProductRequest;
import com.jforce.order.api.dto.ProductResponse;
import com.jforce.order.api.dto.ProductUpdateRequest;
import com.jforce.order.exception.product.ProductNotFoundException;
import com.jforce.order.user.product.entity.Product;
import com.jforce.order.user.product.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<ProductResponse> listEnabled() {

        return productRepository.findByEnabledTrueOrderByNameAsc()
                .stream()
                .map(ProductService::toResponse)
                .toList();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<ProductResponse> listAll() {

        return productRepository.findAllByOrderByNameAsc()
                .stream()
                .map(ProductService::toResponse)
                .toList();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public ProductResponse create(ProductRequest request) {

        Product product = new Product();

        product.setName(request.name().trim());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setQuantity(request.quantity());

        Product savedProduct = productRepository.save(product);
        logger.info("Created product : {}", product);
        return toResponse(savedProduct);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public ProductResponse update(Long id, ProductUpdateRequest request) {

        Product product = getProduct(id);

        product.setPrice(request.price());
        product.setQuantity(request.quantity());

        productRepository.save(product);
        logger.info("Updated product {}", product);
        return toResponse(product);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public ProductResponse setEnabled(Long id, boolean enabled) {

        Product product = getProduct(id);
        product.setEnabled(enabled);

        productRepository.save(product);
        logger.info("Set product {} enabled state to {}", product.getId(), enabled);
        return toResponse(product);
    }

    private Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product " + id + " was not found"));
    }

    public static ProductResponse toResponse(Product product) {
        return new ProductResponse(product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.isEnabled());
    }
}
