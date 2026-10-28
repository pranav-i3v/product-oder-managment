package com.jforce.order.user.cart.repository;

import com.jforce.order.user.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCart_IdAndProduct_Id(Long cartId, Long productId);

    Optional<CartItem> findByIdAndCart_Id(Long id, Long cartId);

    void deleteByCart_Id(Long cartId);
}
