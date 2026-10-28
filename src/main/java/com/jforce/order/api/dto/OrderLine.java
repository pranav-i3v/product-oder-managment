package com.jforce.order.api.dto;

import java.math.BigDecimal;

public record OrderLine(
        Long productId, String productName, int quantity, BigDecimal price, BigDecimal lineTotal) { }
