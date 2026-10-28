package com.jforce.order.user.product.service;

import com.jforce.order.api.dto.ProductRequest;
import com.jforce.order.api.dto.ProductResponse;
import com.jforce.order.api.dto.ProductUpdateRequest;
import com.jforce.order.exception.product.ProductFailedToCreateException;
import com.jforce.order.exception.product.ProductFailedToEnableException;
import com.jforce.order.exception.product.ProductFailedToUpdateException;
import com.jforce.order.exception.product.ProductNotFoundException;
import com.jforce.order.user.product.entity.Product;
import com.jforce.order.user.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productrepository;

    public ProductService(ProductRepository productrepository) {
        this.productrepository = productrepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<ProductResponse> listEnabled() {

        List<ProductResponse> products = productrepository.findByEnabledTrueOrderByNameAsc()
                .stream()
                .map(ProductService::toResponse)
                .toList();

//        if (products.isEmpty()) {
//            throw new ProductNotFoundException("No enabled products found");
//        }

        return products;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<ProductResponse> listAll() {

        List<ProductResponse> products = productrepository.findAll()
                .stream()
                .map(ProductService::toResponse)
                .toList();

//        if (products.isEmpty()) {
//            throw new ProductNotFoundException("No products found");
//        }

        return products;
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public ProductResponse create(ProductRequest request) {

        Product product = new Product();

        product.setName(request.name().trim());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setQuantity(request.quantity());

        try{
            productrepository.save(product);
        } catch (Exception e) {
            throw new ProductFailedToCreateException("Failed to create product: " + request.name() + ", error: " + e.getMessage());
        }

        return toResponse(productrepository.save(product));
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public ProductResponse update(Long id, ProductUpdateRequest request) {

        Product product = getProduct(id);

        product.setPrice(request.price());
        product.setQuantity(request.quantity());

        try{
            productrepository.save(product);
        } catch (Exception e) {
            throw new ProductFailedToUpdateException("Failed to update product: " + product.getName() + ", error: " + e.getMessage());
        }

        return toResponse(product);
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public ProductResponse setEnabled(Long id, boolean enabled) {

        Product product = getProduct(id);
        product.setEnabled(enabled);

        try{
            productrepository.save(product);
        } catch (Exception e) {
            throw new ProductFailedToEnableException("Failed to update product: " + product.getName() + ", error: " + e.getMessage());
        }
        return toResponse(product);
    }

    private Product getProduct(Long id) {
        return productrepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product " + id + " was not found"));
    }

    public static ProductResponse toResponse(Product product) {
        return new ProductResponse(product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.isEnabled(),
                product.getCreatedAt(),
                product.getUpdatedAt());
    }
}
