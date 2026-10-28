package com.jforce.order.user.order.repository;

import com.jforce.order.user.order.entity.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {

    List<CustomerOrder> findByUser_IdOrderByCreatedAtDesc(Long userId);

}
