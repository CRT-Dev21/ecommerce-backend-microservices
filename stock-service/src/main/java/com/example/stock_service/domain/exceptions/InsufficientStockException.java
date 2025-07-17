package com.example.stock_service.domain.exceptions;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String productId, int availableQuantity, int requestedQuantity) {
        super(String.format(
                "Insufficient stock for product %s. Available: %d, Requested: %d",
                productId, availableQuantity, requestedQuantity
        ));
    }
}