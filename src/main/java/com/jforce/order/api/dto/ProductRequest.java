package com.jforce.order.api.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank @Size(max = 255) String name,
        @Size(max = 255) String description,
        @NotNull @DecimalMin("0.00") @Digits(integer = 8, fraction = 2) BigDecimal price,
        @PositiveOrZero int quantity) { }
