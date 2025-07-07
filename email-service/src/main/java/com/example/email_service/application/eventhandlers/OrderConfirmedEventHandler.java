package com.example.email_service.application.eventhandlers;

import com.example.basedomains.events.OrderConfirmedEvent;
import com.example.email_service.domain.service.EmailService;
import com.example.email_service.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderConfirmedEventHandler {
    private final CustomerRepository customerRepository;
    private final EmailService emailService;

    @KafkaListener(topics = "${spring.kafka.topic.name.order.confirmed}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void handleOrderConfirmedEvent(OrderConfirmedEvent event) {
        customerRepository.findByUserId(event.userId())
                .ifPresentOrElse(
                        customer -> emailService.sendOrderConfirmation(
                                customer,
                                event.orderId(),
                                event.items(),
                                event.total()
                        ),
                        () -> log.error("User not found: {}", event.userId())
                );
    }
}
