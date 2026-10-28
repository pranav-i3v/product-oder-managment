package com.jforce.order.api.dto;

import java.math.BigDecimal;

public record CartLine(
        Long productId,
        String name,
        BigDecimal price,
        int quantity,
        BigDecimal lineTotal) { }
