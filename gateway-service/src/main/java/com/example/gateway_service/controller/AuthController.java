package com.example.gateway_service.controller;

import com.example.gateway_service.application.dto.request.UserDto;
import com.example.gateway_service.domain.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<Map<String, String>>> register(@Valid @RequestBody UserDto userDto) {
        return authService.registerUser(userDto)
                .map(message -> ResponseEntity.ok(Map.of("message", message)))
                .onErrorResume(e -> {
                    e.printStackTrace();
                    Map<String, String> error = Map.of("error", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(error));
                });
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<Map<String, String>>> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        return authService.authenticateUser(email, password)
                .map(token -> ResponseEntity.ok(Map.of("token", token)))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
    }
}