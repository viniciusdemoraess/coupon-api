package br.com.couponapi.dtos;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

public record CouponCreateRequest(
        @Schema(
                description = "Código do cupom (6 caracteres alfanuméricos)",
                example = "ABC123"
        )
        String code,

        @Schema(
                description = "Descrição do cupom",
                example = "Desconto de 10% em produtos selecionados"
        )
        String description,

        @Schema(
                description = "Valor do desconto",
                example = "10.00"
        )
        BigDecimal discountValue,

        @Schema(
                description = "Data de expiração do cupom",
                example = "2025-12-31T23:59:59-03:00"
        )
        OffsetDateTime expirationDate,

        @Schema(
                description = "Indica se o cupom está publicado",
                example = "true"
        )
        boolean published
) {}
