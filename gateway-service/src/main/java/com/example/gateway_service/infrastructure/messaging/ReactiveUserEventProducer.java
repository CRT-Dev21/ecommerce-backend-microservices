package com.example.gateway_service.infrastructure.messaging;

import com.example.basedomains.events.UserCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@Service
@Slf4j
public class ReactiveUserEventProducer {

    private final KafkaSender<String, Object> kafkaSender;

    @Value("${spring.kafka.topic.name.user.created}")
    private String userCreatedTopic;

    public ReactiveUserEventProducer(KafkaSender<String, Object> kafkaSender) {
        this.kafkaSender = kafkaSender;
    }

    public Mono<Void> sendUserCreatedEvent(UserCreatedEvent event){
        return kafkaSender.send(Mono.just(SenderRecord.create(userCreatedTopic, null, System.currentTimeMillis(), event.userId().toString(), event, null)))
                .doOnNext(result -> log.info("User created event sent"))
                .then();
    }
}
