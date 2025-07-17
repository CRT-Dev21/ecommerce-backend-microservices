package com.example.gateway_service.repository;

import com.example.gateway_service.model.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, UUID> {
    Mono<User> findByUserId(UUID userId);
    Mono<User> findByEmail(String email);
}