package com.jforce.order.exception.product;

public class ProductInsufficientStockException extends RuntimeException {

    public ProductInsufficientStockException(String message) {
        super(message);
    }

}
