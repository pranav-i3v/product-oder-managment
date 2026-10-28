package com.jforce.order.api.dto;

import jakarta.validation.constraints.Positive;

public record QuantityRequest(@Positive int quantity) { }
