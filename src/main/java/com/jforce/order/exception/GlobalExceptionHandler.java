package com.jforce.order.exception;

import com.jforce.order.exception.cart.CartAddItemException;
import com.jforce.order.exception.cart.CartEmptyException;
import com.jforce.order.exception.cart.CartRemoveItemException;
import com.jforce.order.exception.cart.CartUpdateItemException;
import com.jforce.order.exception.order.OrderPlaceFailedException;
import com.jforce.order.exception.product.ProductFailedToCreateException;
import com.jforce.order.exception.product.ProductFailedToEnableException;
import com.jforce.order.exception.product.ProductFailedToUpdateException;
import com.jforce.order.exception.product.ProductNotFoundException;
import com.jforce.order.exception.user.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst().orElse("Request validation failed");
        return response(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadable(HttpMessageNotReadableException exception) {
        return response(HttpStatus.BAD_REQUEST, "Request body is missing or invalid");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleConflict(DataIntegrityViolationException exception) {
        return response(HttpStatus.CONFLICT, "The request conflicts with existing data");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception exception) {
        logger.error("Unexpected request error", exception);
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiError> handleNoProductFound(ProductNotFoundException exception) {
        return response(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(ProductFailedToCreateException.class)
    public ResponseEntity<ApiError> handleProductCreationFailure(ProductFailedToCreateException exception) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    @ExceptionHandler(ProductFailedToUpdateException.class)
    public ResponseEntity<ApiError> handleProductUpdateFailure(ProductFailedToUpdateException exception) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    @ExceptionHandler(ProductFailedToEnableException.class)
    public ResponseEntity<ApiError> handleProductEnableFailure(ProductFailedToEnableException exception) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFound(UserNotFoundException exception) {
        return response(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(CartAddItemException.class)
    public ResponseEntity<ApiError> handleCartAddItem(CartAddItemException exception) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    @ExceptionHandler(CartUpdateItemException.class)
    public ResponseEntity<ApiError> handleCartUpdateItem(CartUpdateItemException exception) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    @ExceptionHandler(CartRemoveItemException.class)
    public ResponseEntity<ApiError> handleCartRemoveItem(CartRemoveItemException exception) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    @ExceptionHandler(OrderPlaceFailedException.class)
    public ResponseEntity<ApiError> handleOrderPlaceFailure(OrderPlaceFailedException exception) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    @ExceptionHandler(CartEmptyException.class)
    public ResponseEntity<ApiError> handleCartEmpty(CartEmptyException exception) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    private static ResponseEntity<ApiError> response(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message));
    }

    public record ApiError(Instant timestamp, int status, String error, String message) { }
}
