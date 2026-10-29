package com.jforce.order.api.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(List<CartLine> items,
                           BigDecimal total) { }
