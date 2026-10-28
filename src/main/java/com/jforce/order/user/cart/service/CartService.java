package com.jforce.order.user.cart.service;

import com.jforce.order.admin.entity.JForceUser;
import com.jforce.order.admin.repository.UserRepository;
import com.jforce.order.api.dto.CartLine;
import com.jforce.order.api.dto.CartResponse;
import com.jforce.order.exception.cart.CartAddItemException;
import com.jforce.order.exception.cart.CartRemoveItemException;
import com.jforce.order.exception.cart.CartUpdateItemException;
import com.jforce.order.exception.product.ProductDisabledException;
import com.jforce.order.exception.product.ProductInsufficientStockException;
import com.jforce.order.exception.product.ProductNotFoundException;
import com.jforce.order.exception.user.UserNotFoundException;
import com.jforce.order.user.cart.entity.Cart;
import com.jforce.order.user.cart.entity.CartItem;
import com.jforce.order.user.cart.repository.CartItemRepository;
import com.jforce.order.user.cart.repository.CartRepository;
import com.jforce.order.user.product.entity.Product;
import com.jforce.order.user.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository itemRepository;
    private final ProductRepository productRepository;

    public CartService(UserRepository userRepository,
                       CartRepository cartRepository,
                       CartItemRepository itemRepository,
                       ProductRepository productRepository) {

        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.itemRepository = itemRepository;
        this.productRepository = productRepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public CartResponse getCart(String username) {

        return cartRepository.findByUser_Id(findUser(username).getId())
                .map(CartService::toResponse)
                .orElseGet(() -> new CartResponse(List.of(), BigDecimal.ZERO));
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public CartResponse add(String username, Long productId, int quantity) {

        Cart cart = getOrCreateCart(username);
        Product product = getProduct(productId);
        requireAvailable(product, quantity);

        CartItem item = itemRepository.findByCart_IdAndProduct_Id(cart.getId(), productId).orElse(null);

        int existingQuantity = item == null ? 0 : item.getQuantity();

        if (quantity > product.getQuantity() - existingQuantity) {
            throw new ProductInsufficientStockException("Requested cart quantity exceeds available inventory");
        }

        if (item == null) {
            item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
        }

        item.setQuantity(existingQuantity + quantity);

        try {
            itemRepository.save(item);
        } catch (Exception e) {
            throw new CartAddItemException("Failed to add item to cart" + e.getMessage());
        }
        itemRepository.save(item);

        return toResponse(cart);
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public CartResponse update(String username, Long productId, int quantity) {

        Cart cart = getOrCreateCart(username);

        CartItem item = itemRepository.findByCart_IdAndProduct_Id(cart.getId(), productId)
                .orElseThrow(() -> new ProductNotFoundException("Product is not in the cart"));

        requireAvailable(item.getProduct(), quantity);
        item.setQuantity(quantity);

        try {
            itemRepository.save(item);
        } catch (Exception e) {
            throw new CartUpdateItemException("Failed to update item in cart" + e.getMessage());
        }
        return toResponse(cart);
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public CartResponse remove(String username, Long productId) {

        Cart cart = getOrCreateCart(username);

        CartItem item = itemRepository.findByCart_IdAndProduct_Id(cart.getId(), productId)
                .orElseThrow(() -> new ProductNotFoundException("Product is not in the cart"));

        try{
            itemRepository.delete(item);
        } catch (Exception e) {
            throw new CartRemoveItemException("Failed to remove item from cart" + e.getMessage());
        }

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
            throw new ProductDisabledException("Product is disabled");
        }
        if (quantity > product.getQuantity()) {
            throw new ProductInsufficientStockException("Requested quantity exceeds available inventory");
        }
    }

    public static CartResponse toResponse(Cart cart) {

        List<CartLine> lines = cart.getItems().stream()
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
