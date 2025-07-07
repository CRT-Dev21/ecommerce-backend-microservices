package com.example.gateway_service.domain.service;

import com.example.basedomains.events.UserCreatedEvent;
import com.example.gateway_service.application.dto.request.UserDto;
import com.example.gateway_service.application.mapper.UserMapper;
import com.example.gateway_service.config.jwt.JwtUtil;
import com.example.gateway_service.domain.exception.UserNotFoundException;
import com.example.gateway_service.infrastructure.messaging.ReactiveUserEventProducer;
import com.example.gateway_service.model.User;
import com.example.gateway_service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ReactiveUserEventProducer eventProducer;

    public AuthService(ReactiveUserEventProducer eventProducer, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.eventProducer = eventProducer;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Mono<String> registerUser(UserDto userDto) {
        return userRepository.findByEmail(userDto.getEmail())
                .flatMap(existingUser -> Mono.error(new RuntimeException("The user already exists")))
                .switchIfEmpty(Mono.defer(() -> {
                    User user = UserMapper.mapDtoToUser(userDto);
                    user.setPassword(passwordEncoder.encode(userDto.getPassword()));

                    return userRepository.save(user)
                            .flatMap(savedUser -> {
                                return eventProducer.sendUserCreatedEvent(
                                        new UserCreatedEvent(
                                                savedUser.getUserId(),
                                                savedUser.getEmail(),
                                                savedUser.getName(),
                                                true,
                                                savedUser.getRole()
                                        )
                                ).thenReturn(savedUser);
                            })
                            .map(jwtUtil::generateToken);
                }))
                .cast(String.class);
    }

    public Mono<String> authenticateUser(String email, String password) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new UserNotFoundException(email)))
                .flatMap(user -> {
                    if (!passwordEncoder.matches(password, user.getPassword())) {
                        return Mono.error(new RuntimeException("Invalid credentials"));
                    }
                    return Mono.just(jwtUtil.generateToken(user));
                });
    }
}

