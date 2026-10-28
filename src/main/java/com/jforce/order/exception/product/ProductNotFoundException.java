package com.jforce.order.exception.product;

public class ProductNotFoundException extends RuntimeException {

    static final long serialVersionUID = 1L;

    public ProductNotFoundException(String message) {
        super(message);
    }

}
