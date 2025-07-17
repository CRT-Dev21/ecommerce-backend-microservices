package com.example.order_service.application.dto.request;

import com.example.basedomains.dto.request.ItemOrderRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record OrderRequest(
        @NotEmpty List<@Valid ItemOrderRequest> items,
        @Positive BigDecimal total,
        @NotBlank String shippingAddress
) {}