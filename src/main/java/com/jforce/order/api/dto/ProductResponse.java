package com.jforce.order.api.dto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        int quantity,
        boolean enabled,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt) { }
