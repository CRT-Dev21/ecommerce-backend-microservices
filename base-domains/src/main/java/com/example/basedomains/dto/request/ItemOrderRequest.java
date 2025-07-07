package com.example.basedomains.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record ItemOrderRequest(
        @NotBlank String productCode,
        @NotBlank String productName,
        @Positive Integer qty
) {}
