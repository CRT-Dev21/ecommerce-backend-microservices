package com.example.basedomains.events;

import com.example.basedomains.dto.request.ItemOrderRequest;

import java.util.List;

public record ReservedStockEvent(Long orderId, boolean reserved, String reason, List<ItemOrderRequest> items, double total) {}
