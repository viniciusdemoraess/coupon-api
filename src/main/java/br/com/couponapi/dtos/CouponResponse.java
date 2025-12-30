package br.com.couponapi.dtos;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CouponResponse(
        String id,
        String code,
        String description,
        BigDecimal discountValue,
        OffsetDateTime expirationDate,
        String status,
        boolean published,
        boolean redeemed
) {}
