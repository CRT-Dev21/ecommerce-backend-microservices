package com.example.stock_service.repository;

import com.example.stock_service.model.ProductStock;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ProductStockRepository extends ReactiveCrudRepository<ProductStock, Long>, CustomProductStockRepository {

    @Query("SELECT * FROM product_stocks WHERE seller_id = :id")
    Flux<ProductStock> findBySellerId(@Param("id") UUID sellerId);

    @Query("SELECT * FROM product_stocks WHERE product_code = :code")
    Mono<ProductStock> findByProductCode(@Param("code") String productCode);

}
