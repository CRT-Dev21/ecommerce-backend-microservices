package com.example.stock_service.domain.service;

import com.example.basedomains.dto.request.ItemOrderRequest;
import com.example.basedomains.events.OrderCreatedEvent;
import com.example.basedomains.events.ReservedStockEvent;
import com.example.stock_service.domain.exceptions.InsufficientStockException;
import com.example.stock_service.domain.exceptions.ProductNotFoundException;
import com.example.stock_service.infrastructure.messaging.ReactiveStockEventProducer;
import com.example.stock_service.repository.ProductStockRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class StockService {

    private final ProductStockRepository productStockRepository;
    private final ReactiveStockEventProducer reactiveStockEventProducer;

    public StockService(ProductStockRepository productStockRepository, ReactiveStockEventProducer reactiveStockEventProducer) {
        this.productStockRepository = productStockRepository;
        this.reactiveStockEventProducer = reactiveStockEventProducer;
    }

    public Mono<Void> processOrderCreatedEvent(OrderCreatedEvent event) {
        return checkStockAvailability(event.items())
                .then(reduceStockQuantities(event.items()))
                .then(getTotalOfTheOrder(event.items()))
                .flatMap(total -> reactiveStockEventProducer.publishStockReserved(
                        new ReservedStockEvent(
                                event.orderId(),
                                true,
                                "Stock reserved and reduced successfully",
                                event.items(),
                                total,
                                event.userEmail()
                        )
                ))
                .onErrorResume(e -> {
                    if (e instanceof ProductNotFoundException) {
                        return reactiveStockEventProducer.publishStockReservationFailed(
                                event.orderId(),
                                "Product not found: " + e.getMessage()
                        );
                    } else if (e instanceof InsufficientStockException) {
                        return reactiveStockEventProducer.publishStockReservationFailed(
                                event.orderId(),
                                "No longer available in stock"
                        );
                    } else {
                        log.error("Unexpected error processing order {}: {}", event.orderId(), e.getMessage());
                        return reactiveStockEventProducer.publishStockReservationFailed(
                                event.orderId(),
                                "Internal server error"
                        );
                    }
                })
                .then();
    }

    private Mono<Void> checkStockAvailability(List<ItemOrderRequest> items) {
        return Flux.fromIterable(items)
                .flatMap(item -> productStockRepository.findByProductCode(item.productCode())
                        .switchIfEmpty(Mono.error(new ProductNotFoundException(item.productCode())))
                        .flatMap(product -> {
                            if (item.qty() > product.getQty()) {

                                return Mono.error(new InsufficientStockException(
                                        item.productCode(), product.getQty(), item.qty()));
                            }
                            return Mono.just(product);
                        }))
                .then();
    }

    private Mono<Void> reduceStockQuantities(List<ItemOrderRequest> items) {
        return Flux.fromIterable(items)
                .flatMap(item ->
                        productStockRepository.tryReduceStock(item.productCode(), item.qty())
                                .flatMap(success -> {
                                    if (!success) {
                                        return Mono.error(new InsufficientStockException(
                                                item.productCode(), -1, item.qty()));
                                    }
                                    return Mono.empty();
                                })
                )
                .then();
    }


    private Mono<Double> getTotalOfTheOrder(List<ItemOrderRequest> items) {
        return Flux.fromIterable(items)
                .flatMap(item ->
                        productStockRepository.findByProductCode(item.productCode())
                                .switchIfEmpty(Mono.error(new ProductNotFoundException(item.productCode())))
                                .map(product -> product.getPrice() * item.qty())
                )
                .reduce(0.0, Double::sum);
    }
}