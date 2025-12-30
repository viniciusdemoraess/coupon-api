package br.com.couponapi.dtos;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CouponCreateRequest(
        String code,
        String description,
        BigDecimal discountValue,
        OffsetDateTime expirationDate,
        boolean published
) {}
