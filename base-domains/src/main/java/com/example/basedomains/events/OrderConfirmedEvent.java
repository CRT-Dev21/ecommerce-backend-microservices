package com.example.basedomains.events;

import com.example.basedomains.dto.request.ItemOrderRequest;

import java.util.List;
import java.util.UUID;

public record OrderConfirmedEvent(Long orderId, UUID userId, List<ItemOrderRequest> items, double total) {}
