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
    public List<ProductResponse> listAllProducts() {
        return productService.listAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@Valid @RequestBody ProductRequest request) {

        logger.info("Creating product {}", request);
        return productService.create(request);
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable Long id,
                                  @Valid @RequestBody ProductUpdateRequest request) {

        logger.info("Updating product {}", request);
        return productService.update(id, request);
    }

    @PatchMapping("/{id}/enabled")
    public ProductResponse setEnabled(@PathVariable Long id,
                                      @Valid @RequestBody EnabledRequest request) {

        logger.info("Setting enabled status for product {}", request);
        return productService.setEnabled(id, request.enabled());
    }
}
