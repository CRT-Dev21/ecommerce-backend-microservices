package com.example.stock_service.application.dto.request;

import lombok.Data;

@Data
public class CreateProductRequest {
    private String productName;
    private String description;
    private Double price;
    private Integer qty;
    private String category;
    private Integer minimumStock;
    private String location;
}