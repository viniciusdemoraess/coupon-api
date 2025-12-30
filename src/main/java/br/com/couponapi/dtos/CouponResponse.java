package br.com.couponapi.dtos;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

public record CouponResponse(
        @Schema(example = "598c5a85-46d6-4c69-8513-6ecfb9b5d7e2")
        String id,

        @Schema(example = "ABC123")
        String code,

        @Schema(example = "Desconto de lançamento")
        String description,

        @Schema(example = "10.0")
        BigDecimal discountValue,

        @Schema(example = "2025-12-31T23:59:59-03:00")
        OffsetDateTime expirationDate,

        @Schema(example = "ACTIVE")
        String status,

        @Schema(example = "true")
        boolean published,
        
        @Schema(example = "false")
        boolean redeemed
) {}
