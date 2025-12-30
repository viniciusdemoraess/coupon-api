package com.example.couponapi.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CouponCreateRequest(
    @NotBlank(message = "Code is required")
    String code,

    @NotBlank(message = "Description is required")
    String description,

    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.5", inclusive = true, message = "Discount value must be at least 0.5")
    BigDecimal discountValue,

    @NotNull(message = "Expiration date is required")
    OffsetDateTime expirationDate,

    boolean published
) {}