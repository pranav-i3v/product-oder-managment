package com.jforce.order.api.dto;

import jakarta.validation.constraints.NotNull;

public record EnabledRequest(@NotNull Boolean enabled) { }
