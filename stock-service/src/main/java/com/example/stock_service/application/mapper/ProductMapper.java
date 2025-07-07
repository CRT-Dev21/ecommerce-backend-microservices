package com.example.stock_service.application.mapper;

import com.example.stock_service.application.dto.request.CreateProductRequest;
import com.example.stock_service.application.dto.response.ProductResponse;
import com.example.stock_service.model.ProductStock;

import java.util.UUID;

public class ProductMapper {
    public static ProductResponse mapperProductStockToResponse(ProductStock productStock){
        ProductResponse response = new ProductResponse();

        response.setProductId(productStock.getId());
        response.setProductCode(productStock.getProductCode());
        response.setName(productStock.getProductName());
        response.setCategory(productStock.getCategory());
        response.setPrice(productStock.getPrice());
        response.setDescription(productStock.getDescription());
        response.setImageUrl(productStock.getImageName());
        response.setStock(productStock.getQty());

        return response;
    }

    public static ProductStock mapperRequestToProductStock(CreateProductRequest request, UUID sellerId){
        ProductStock product = new ProductStock();

        product.setProductName(request.getProductName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQty(request.getQty());
        product.setCategory(request.getCategory());
        product.setMinimumStock(request.getMinimumStock());
        product.setLocation(request.getLocation());
        product.setSellerId(sellerId);

        return product;
    }
}
