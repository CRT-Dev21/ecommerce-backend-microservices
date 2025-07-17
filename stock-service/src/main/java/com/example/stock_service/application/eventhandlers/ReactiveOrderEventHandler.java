package com.example.stock_service.application.eventhandlers;

import com.example.basedomains.events.OrderCreatedEvent;
import com.example.stock_service.domain.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.ReceiverRecord;

import java.util.function.Consumer;

@Service
@Slf4j
public class ReactiveOrderEventHandler {
    private final StockService stockService;

    public ReactiveOrderEventHandler(StockService stockService) {
        this.stockService = stockService;
    }

    @Bean
    public Consumer<Flux<ReceiverRecord<String, OrderCreatedEvent>>> handleOrderCreatedEvent(){
        return flux -> flux
                .flatMap(record -> stockService.processOrderCreatedEvent(record.value())
                        .then(record.receiverOffset().commit()))
                .subscribe();
    }
}