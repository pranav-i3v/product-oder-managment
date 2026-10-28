package com.jforce.order.user.cart.entity;

import com.jforce.order.user.product.entity.Product;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Entity
@NoArgsConstructor
@Table(schema = "jf", name = "cart_items",
        uniqueConstraints = @UniqueConstraint(name = "uk_cart_item_product", columnNames = {"cart_id", "product_id"}))
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    @PrePersist
    void onCreate() {
        ZonedDateTime now = ZonedDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = ZonedDateTime.now();
    }

}
