package com.example.email_service.application.eventhandlers;

import com.example.basedomains.events.UserCreatedEvent;
import com.example.email_service.model.Customer;
import com.example.email_service.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserEventHandler {

    private final CustomerRepository customerRepository;

    public UserEventHandler(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @KafkaListener(
            topics = "${spring.kafka.topic.name.user.created}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void handleUserEvent(UserCreatedEvent event) {
        Customer customer = new Customer(event.userId(), event.email(), event.name(), event.isActive(), event.role());
        log.info("Received UserCreatedEvent from Gateway service {}", event.userId());
        customerRepository.save(customer);
    }
}
