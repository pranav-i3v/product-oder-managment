package com.jforce.order.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProductUpdateRequest(
        @NotNull @DecimalMin("0.00") @Digits(integer = 8, fraction = 2) BigDecimal price,
        @PositiveOrZero int quantity) { }
