package com.jforce.order.user.order.controller;

import com.jforce.order.api.dto.OrderResponse;
import com.jforce.order.user.order.service.OrderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public OrderResponse placeOrder(@AuthenticationPrincipal UserDetails user) {
        return orderService.placeOrder(user.getUsername());
    }

    @GetMapping
    public List<OrderResponse> listOrders(@AuthenticationPrincipal UserDetails user) {
        return orderService.listOrders(user.getUsername());
    }
}
