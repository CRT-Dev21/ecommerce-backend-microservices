package com.example.order_service.application.eventhandlers;

import com.example.basedomains.events.OrderConfirmedEvent;
import com.example.basedomains.events.ReservedStockEvent;
import com.example.order_service.domain.exceptions.OrderNotFoundException;
import com.example.order_service.infrastructure.messaging.OrderEventProducer;
import com.example.order_service.model.Order;
import com.example.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockEventHandler {
    private final OrderRepository orderRepository;
    private final OrderEventProducer eventProducer;

    @KafkaListener(topics = "${spring.kafka.topic.name.stock}")
    public void handleStockResponse(ReservedStockEvent event) {
        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new OrderNotFoundException(event.orderId()));

        if (event.reserved()) {
            order.setStatus("CONFIRMED");
            eventProducer.sendOrderConfirmedEvent(
                    new OrderConfirmedEvent(order.getId(), order.getUserId(), event.items(), event.total()));
        } else {
            order.setStatus("REJECTED");
            order.setRejectionReason(event.reason());
        }

        orderRepository.save(order);
    }
}
