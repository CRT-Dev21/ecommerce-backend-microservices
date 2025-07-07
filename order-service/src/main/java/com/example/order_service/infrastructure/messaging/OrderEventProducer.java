package com.example.order_service.infrastructure.messaging;

import com.example.basedomains.events.OrderConfirmedEvent;
import com.example.basedomains.events.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic.name.order}")
    private String orderTopic;

    @Value("${spring.kafka.topic.name.order.confirmed}")
    private String orderConfirmedTopic;

    public void sendOrderCreatedEvent(OrderCreatedEvent event) {
        kafkaTemplate.send(orderTopic, event);
        log.info("Sent OrderCreatedEvent for order {}", event.orderId());
    }

    public void sendOrderConfirmedEvent(OrderConfirmedEvent event) {
        kafkaTemplate.send(orderConfirmedTopic, event);
        log.info("Sent OrderConfirmedEvent for order {}", event.orderId());
    }
}
