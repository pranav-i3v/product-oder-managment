package com.jforce.order.user.cart.service;

import com.jforce.order.admin.entity.JForceUser;
import com.jforce.order.admin.repository.UserRepository;
import com.jforce.order.api.dto.CartLine;
import com.jforce.order.api.dto.CartResponse;
import com.jforce.order.exception.product.ProductDisabledException;
import com.jforce.order.exception.product.ProductInsufficientStockException;
import com.jforce.order.exception.product.ProductNotFoundException;
import com.jforce.order.exception.user.UserNotFoundException;
import com.jforce.order.user.cart.entity.Cart;
import com.jforce.order.user.cart.entity.CartItem;
import com.jforce.order.user.cart.repository.CartRepository;
import com.jforce.order.user.product.entity.Product;
import com.jforce.order.user.product.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(UserRepository userRepository,
                       CartRepository cartRepository,
                       ProductRepository productRepository) {

        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public CartResponse getCart(String username) {

        return cartRepository.findByUser_Id(findUser(username).getId())
                .map(CartService::toResponse)
                .orElseGet(() -> new CartResponse(List.of(), BigDecimal.ZERO));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public CartResponse add(String username, Long productId, int quantity) {

        Cart cart = getOrCreateCart(username);
        Product product = getProduct(productId);

        CartItem item = findItem(cart, productId).orElse(null);
        int newQuantity = (item == null ? 0 : item.getQuantity()) + quantity;

        requireAvailable(product, newQuantity);

        if (item == null) {
            item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setUseInd("Y");
            cart.getItems().add(item);
        }

        item.setQuantity(newQuantity);
        logger.info("Added quantity {} of product {} to cart", quantity, productId);

        return toResponse(cart);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public CartResponse update(String username, Long productId, int quantity) {

        Cart cart = getExistingCart(username);

        CartItem item = findItem(cart, productId)
                .orElseThrow(() -> new ProductNotFoundException("Product is not in the cart"));

        requireAvailable(item.getProduct(), quantity);
        item.setQuantity(quantity);

        logger.info("Updated cart quantity for product {} to {}", productId, quantity);
        return toResponse(cart);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public CartResponse remove(String username, Long productId) {

        Cart cart = getExistingCart(username);

        CartItem item = findItem(cart, productId)
                .orElseThrow(() -> new ProductNotFoundException("Product is not in the cart"));

        item.setUseInd("N");

        logger.info("Removed product {} from cart", productId);
        return toResponse(cart);
    }

    private Cart getOrCreateCart(String username) {

        JForceUser user = findUser(username);

        return cartRepository.findByUser_Id(user.getId()).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setUser(user);
            return cartRepository.save(cart);
        });
    }

    private Cart getExistingCart(String username) {
        return cartRepository.findByUser_Id(findUser(username).getId())
                .orElseThrow(() -> new ProductNotFoundException("Product is not in the cart"));
    }

    private static Optional<CartItem> findItem(Cart cart, Long productId) {
        return cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
    }

    private JForceUser findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User was not found"));
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product " + productId + " was not found"));
    }

    private static void requireAvailable(Product product, int quantity) {
        if (!product.isEnabled()) {
            logger.warn("Attempted to add disabled product {} to cart", product.getId());
            throw new ProductDisabledException("Product is disabled");
        }
        if (quantity > product.getQuantity()) {
            logger.warn("Requested quantity {} exceeds available stock for product {}", quantity, product.getId());
            throw new ProductInsufficientStockException("Requested quantity exceeds available inventory");
        }
    }

    public static CartResponse toResponse(Cart cart) {

        List<CartLine> lines = cart.getItems().stream()
                .filter(CartItem::isActive)
                .map(item -> {

                    Product product = item.getProduct();
                    BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

                    return new CartLine(product.getId(),
                            product.getName(),
                            product.getPrice(),
                            item.getQuantity(),
                            lineTotal);
                }).toList();

        BigDecimal total = lines.stream()
                .map(CartLine::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(lines, total);
    }
}
