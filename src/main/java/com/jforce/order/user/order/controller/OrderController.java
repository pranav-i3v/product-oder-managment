package com.jforce.order.user.order.controller;

import com.jforce.order.api.dto.OrderResponse;
import com.jforce.order.user.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse placeOrder(@AuthenticationPrincipal UserDetails user) {
        logger.info("Placing order for user: {}", user.getUsername());
        return orderService.placeOrder(user.getUsername());
    }

    @GetMapping
    public List<OrderResponse> listOrders(@AuthenticationPrincipal UserDetails user) {
        logger.info("Listing orders for user: {}", user.getUsername());
        return orderService.listOrders(user.getUsername());
    }
}
