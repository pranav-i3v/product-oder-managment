package com.jforce.order.user.cart.controller;

import com.jforce.order.api.dto.CartResponse;
import com.jforce.order.api.dto.QuantityRequest;
import com.jforce.order.user.cart.service.CartService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public CartResponse getCart(@AuthenticationPrincipal UserDetails user) {

        return cartService.getCart(user.getUsername());
    }

    @PostMapping("/items/{productId}")
    public CartResponse addItem(@AuthenticationPrincipal UserDetails user,
                                @PathVariable Long productId,
                                @Valid @RequestBody QuantityRequest request) {

        logger.info("Adding item to cart: productId={}, quantity={}", productId, request.quantity());

        return cartService.add(user.getUsername(), productId, request.quantity());
    }

    @PutMapping("/items/{productId}")
    public CartResponse updateItem(@AuthenticationPrincipal UserDetails user,
                                   @PathVariable Long productId,
                                   @Valid @RequestBody QuantityRequest request) {

        logger.info("Updating item in cart: productId={}, quantity={}", productId, request.quantity());

        return cartService.update(user.getUsername(), productId, request.quantity());
    }

    @DeleteMapping("/items/{productId}")
    public CartResponse removeItem(@AuthenticationPrincipal UserDetails user,
                                   @PathVariable Long productId) {

        logger.info("Removing item from cart: productId={}", productId);

        return cartService.remove(user.getUsername(), productId);
    }
}
