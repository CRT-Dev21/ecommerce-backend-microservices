package com.example.stock_service.infrastructure.messaging;

import com.example.basedomains.events.ReservedStockEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.util.ArrayList;

@Service
@Slf4j
public class ReactiveStockEventProducer {

    private final KafkaSender <String, Object> kafkaSender;

    @Value("${spring.kafka.topic.name.stock}")
    private String stockTopic;

    public ReactiveStockEventProducer(KafkaSender<String, Object> kafkaSender) {
        this.kafkaSender = kafkaSender;
    }

    public Mono<Void> publishStockReserved(ReservedStockEvent reservedStockEvent){
        return kafkaSender.send(Mono.just(SenderRecord.create(stockTopic, null, System.currentTimeMillis(), reservedStockEvent.orderId().toString(), reservedStockEvent, null)))
                .doOnNext(result -> log.info("Sent stock reserved for order: {}", reservedStockEvent.orderId()))
                .then();
    }

    public Mono<Void> publishStockReservationFailed(Long orderId, String reason){
        return kafkaSender.send(Mono.just(SenderRecord.create(stockTopic, null, System.currentTimeMillis(), orderId.toString(), new ReservedStockEvent(orderId, false, reason, new ArrayList<>(), 0, null), null)))
                .doOnNext(result -> log.warn("Publishing stock reservation failed for order {}: {}", orderId, reason))
                .then();
    }
}