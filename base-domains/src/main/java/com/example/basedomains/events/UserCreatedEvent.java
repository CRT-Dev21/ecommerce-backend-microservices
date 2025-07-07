package com.example.basedomains.events;

import java.util.UUID;

public record UserCreatedEvent(UUID userId, String email, String name, boolean isActive, String role) {}
