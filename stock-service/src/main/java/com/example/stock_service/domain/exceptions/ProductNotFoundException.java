package com.example.stock_service.domain.exceptions;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String productId) {
        super(String.format("Product with ID %s not found", productId));
    }
}
