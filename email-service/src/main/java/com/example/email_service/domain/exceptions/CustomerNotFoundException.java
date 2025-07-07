package com.example.email_service.domain.exceptions;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String userId){
        super("User not found with id: " + userId);
    }
}
