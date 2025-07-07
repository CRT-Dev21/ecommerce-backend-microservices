package com.example.stock_service.config.kafka;

import com.example.basedomains.events.OrderCreatedEvent;
import com.example.stock_service.domain.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class ReactiveKafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;


    // Configuration for the reactive producer
    @Bean
    public SenderOptions<String, Object> producerOptions(){
        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return SenderOptions.create(props);
    }

    @Bean
    public KafkaSender<String, Object> kafkaSender(){
        return KafkaSender.create(producerOptions());
    }

    // Configuration for the consumer
    @Bean
    public ReceiverOptions<String, OrderCreatedEvent> consumerOptions(
            @Value("${spring.kafka.topic.name.order.created}") String topic) {

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "reactive-stock-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, OrderCreatedEvent.class.getName());

        return ReceiverOptions.<String, OrderCreatedEvent>create(props)
                .subscription(Collections.singletonList(topic));
    }

    @Bean
    public KafkaReceiver<String, OrderCreatedEvent> kafkaReceiver(
            ReceiverOptions<String, OrderCreatedEvent> receiverOptions) {
        return KafkaReceiver.create(receiverOptions);
    }

    @Bean
    public ApplicationRunner kafkaConsumerRunner(
            KafkaReceiver<String, OrderCreatedEvent> receiver,
            StockService stockService) {

        return args -> {
            receiver.receive()
                    .doOnNext(record -> log.info("Received event: {}", record.value()))
                    .flatMap(record -> {
                        OrderCreatedEvent event = record.value();
                        return stockService.processOrderCreatedEvent(event)
                                .then(record.receiverOffset().commit());
                    })
                    .doOnError(e -> log.error("Error processing message: {}", e.getMessage()))
                    .retry()
                    .subscribe();
        };
    }
}
