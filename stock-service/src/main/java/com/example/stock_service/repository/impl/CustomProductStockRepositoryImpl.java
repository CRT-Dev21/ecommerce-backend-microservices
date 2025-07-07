package com.example.stock_service.repository.impl;

import com.example.stock_service.model.ProductStock;
import com.example.stock_service.repository.CustomProductStockRepository;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class CustomProductStockRepositoryImpl implements CustomProductStockRepository {

    private final DatabaseClient databaseClient;

    public CustomProductStockRepositoryImpl(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Flux<ProductStock> findAvailableProducts(int limit, int offset) {
        return databaseClient.sql("SELECT * FROM product_stocks WHERE qty > 0 ORDER BY id LIMIT $1 OFFSET $2")
                .bind("$1", limit)
                .bind("$2", offset)
                .map((row, meta) -> {
                    ProductStock p = new ProductStock();
                    p.setId(row.get("id", Long.class));
                    p.setProductCode(row.get("product_code", String.class));
                    p.setProductName(row.get("product_name", String.class));
                    p.setDescription(row.get("description", String.class));
                    p.setQty(row.get("qty", Integer.class));
                    p.setPrice(row.get("price", Double.class));
                    p.setImageName(row.get("image_name", String.class));
                    p.setCategory(row.get("category", String.class));
                    p.setSellerId(row.get("seller_id", UUID.class));
                    p.setLocation(row.get("location", String.class));
                    return p;
                })
                .all();
    }

    @Override
    public Mono<Boolean> tryReduceStock(String productCode, int qty) {
        return databaseClient.sql("""
            UPDATE product_stocks
            SET qty = qty - $1,
                version = version + 1
            WHERE product_code = $2
              AND qty >= $1
        """)
                .bind("$1", qty)
                .bind("$2", productCode)
                .fetch()
                .rowsUpdated()
                .map(updatedRows -> updatedRows > 0);
    }
}