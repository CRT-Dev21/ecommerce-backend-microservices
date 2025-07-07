package com.example.order_service.application.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderResponse(
        Long orderId,
        String status,
        String rejectionReason,
        BigDecimal total,
        String shippingAddress,
        Instant createdAt,
        String formattedCreatedAt
) {}
