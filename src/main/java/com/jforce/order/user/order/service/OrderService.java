package com.jforce.order.user.order.service;

import com.jforce.order.admin.entity.JForceUser;
import com.jforce.order.admin.repository.UserRepository;
import com.jforce.order.api.dto.OrderLine;
import com.jforce.order.api.dto.OrderResponse;
import com.jforce.order.exception.cart.CartEmptyException;
import com.jforce.order.exception.order.OrderPlaceFailedException;
import com.jforce.order.exception.product.ProductInsufficientStockException;
import com.jforce.order.exception.product.ProductNotFoundException;
import com.jforce.order.exception.user.UserNotFoundException;
import com.jforce.order.user.cart.entity.Cart;
import com.jforce.order.user.cart.entity.CartItem;
import com.jforce.order.user.cart.repository.CartItemRepository;
import com.jforce.order.user.cart.repository.CartRepository;
import com.jforce.order.user.order.entity.CustomerOrder;
import com.jforce.order.user.order.entity.OrderItem;
import com.jforce.order.user.order.repository.CustomerOrderRepository;
import com.jforce.order.user.product.entity.Product;
import com.jforce.order.user.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
public class OrderService {

    private final UserRepository users;
    private final CartRepository carts;
    private final CartItemRepository cartItems;
    private final ProductRepository products;
    private final CustomerOrderRepository orders;

    public OrderService(UserRepository users,
                        CartRepository carts,
                        CartItemRepository cartItems,
                        ProductRepository products,
                        CustomerOrderRepository orders) {

        this.users = users;
        this.carts = carts;
        this.cartItems = cartItems;
        this.products = products;
        this.orders = orders;
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public OrderResponse placeOrder(String username) {
        CustomerOrder savedOrder = null;
        try {
            JForceUser user = findUser(username);

            Cart cart = carts.findByUser_Id(user.getId())
                    .orElseThrow(() -> new CartEmptyException("Cart is empty"));

            List<CartItem> requestedItems = cart.getItems().stream()
                    .sorted(Comparator.comparing(item -> item.getProduct().getId()))
                    .toList();

            if (requestedItems.isEmpty()) {
                throw new CartEmptyException("Cart is empty");
            }

            CustomerOrder order = new CustomerOrder();
            order.setUser(user);
            BigDecimal total = BigDecimal.ZERO;

            for (CartItem cartItem : requestedItems) {

                Product product = products.findByIdForUpdate(cartItem.getProduct().getId())
                        .orElseThrow(() -> new ProductNotFoundException("Product was not found"));

                int quantity = cartItem.getQuantity();

                if (!product.isEnabled() || product.getQuantity() < quantity) {
                    throw new ProductInsufficientStockException("Insufficient inventory for product: " + product.getName());
                }

                BigDecimal price = product.getPrice();
                total = total.add(price.multiply(BigDecimal.valueOf(quantity)));
                product.setQuantity(product.getQuantity() - quantity);

                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProduct(product);
                orderItem.setQuantity(quantity);
                orderItem.setPrice(price);
                order.getItems().add(orderItem);
            }

            order.setTotalPrice(total);

            savedOrder = orders.save(order);
            cartItems.deleteByCart_Id(cart.getId());

        } catch (Exception e) {
            throw new OrderPlaceFailedException("Failed to place order: " + e.getMessage());
        }

        return toResponse(savedOrder);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<OrderResponse> listOrders(String username) {

        JForceUser user = findUser(username);

        return orders.findByUser_IdOrderByCreatedAtDesc(user.getId()).stream()
                .map(OrderService::toResponse)
                .toList();
    }

    private JForceUser findUser(String username) {
        return users.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User was not found"));
    }

    private static OrderResponse toResponse(CustomerOrder order) {

        List<OrderLine> lines = order.getItems().stream()
                .map(item -> new OrderLine(item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))))
                .toList();

        return new OrderResponse(order.getId(),
                order.getStatus().name(),
                order.getTotalPrice(),
                order.getCreatedAt(),
                lines);
    }
}
