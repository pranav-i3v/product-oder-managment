package com.jforce.order.user.product.controller;

import com.jforce.order.api.dto.EnabledRequest;
import com.jforce.order.api.dto.ProductRequest;
import com.jforce.order.api.dto.ProductResponse;
import com.jforce.order.api.dto.ProductUpdateRequest;
import com.jforce.order.user.product.service.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private static final Logger logger = LoggerFactory.getLogger(AdminProductController.class);

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> listAllProducts() {
        return ResponseEntity.ok(productService.listAll());
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {

        logger.info("Creating product {}", request);
        ProductResponse product = productService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody ProductUpdateRequest request) {

        logger.info("Updating product {}", request);
        return ResponseEntity.ok(productService.update(id, request));
    }

    @PatchMapping("/{id}/enabled")
    public ResponseEntity<ProductResponse> setEnabled(@PathVariable Long id,
                                                    @Valid @RequestBody EnabledRequest request) {

        logger.info("Setting enabled status for product {}", request);
        return ResponseEntity.ok(productService.setEnabled(id, request.enabled()));
    }
}
