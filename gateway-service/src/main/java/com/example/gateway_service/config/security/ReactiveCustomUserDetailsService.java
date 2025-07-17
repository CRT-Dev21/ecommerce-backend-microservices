package com.example.gateway_service.config.security;

import com.example.gateway_service.repository.UserRepository;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class ReactiveCustomUserDetailsService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;

    public ReactiveCustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String userId) {
        try {
            return userRepository.findByUserId(UUID.fromString(userId))
                    .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found with id: " + userId)))
                    .map(user -> org.springframework.security.core.userdetails.User.builder()
                            .username(user.getUserId().toString())
                            .password(user.getPassword())
                            .roles(user.getRole())
                            .build());
        } catch (IllegalArgumentException e) {
            return Mono.error(new Exception("Error searching user with id: " + userId));
        }
    }
}