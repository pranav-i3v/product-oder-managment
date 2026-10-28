package com.jforce.order.exception.cart;

public class CartEmptyException extends RuntimeException {

    public CartEmptyException(String message) {
        super(message);
    }

}
