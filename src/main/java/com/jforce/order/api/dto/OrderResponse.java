package com.jforce.order.api.dto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String status,
        BigDecimal totalPrice,
        ZonedDateTime createdAt,
        List<OrderLine> items) { }
