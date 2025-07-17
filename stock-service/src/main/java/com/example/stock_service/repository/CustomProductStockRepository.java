package com.example.stock_service.repository;

import com.example.stock_service.model.ProductStock;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomProductStockRepository {
    Flux<ProductStock> findAvailableProducts(int limit, int offset);

    Mono<Boolean> tryReduceStock(String productCode, int qty);
}