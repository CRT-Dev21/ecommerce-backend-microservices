package com.example.stock_service.application.dto.response;

import com.example.stock_service.model.ProductStock;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private Long productId;

    private String productCode;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String category;
    private String imageUrl;

    public ProductResponse(ProductStock productStock) {
        this.productId = productStock.getId();

        this.productCode = productStock.getProductCode();
        this.name = productStock.getProductName();
        this.description = productStock.getDescription();
        this.price = productStock.getPrice();
        this.stock = productStock.getQty();
        this.category = productStock.getCategory();
        this.imageUrl = productStock.getImageName();
    }
}