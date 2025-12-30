package com.example.couponapi.service;

import com.example.couponapi.dto.CouponCreateRequest;
import com.example.couponapi.dto.CouponResponse;
import com.example.couponapi.model.Coupon;
import com.example.couponapi.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    @Transactional
    public CouponResponse createCoupon(CouponCreateRequest request) {
        // Process code: remove special characters and take first 6 alphanumeric chars
        String processedCode = request.code().replaceAll("[^a-zA-Z0-9]", "").substring(0, Math.min(6, request.code().replaceAll("[^a-zA-Z0-9]", "").length()));
        if (processedCode.length() < 6) {
            throw new IllegalArgumentException("Code must have at least 6 alphanumeric characters after processing");
        }
        processedCode = processedCode.substring(0, 6).toUpperCase(); // Ensure uppercase and exactly 6

        // Check if code already exists
        if (couponRepository.findByCodeActive(processedCode).isPresent()) {
            throw new IllegalArgumentException("Coupon code already exists");
        }

        // Validate discount value
        if (request.discountValue().compareTo(BigDecimal.valueOf(0.5)) < 0) {
            throw new IllegalArgumentException("Discount value must be at least 0.5");
        }

        // Validate expiration date
        if (request.expirationDate().isBefore(OffsetDateTime.now())) {
            throw new IllegalArgumentException("Expiration date must be in the future");
        }

        Coupon coupon = new Coupon();
        coupon.setCode(processedCode);
        coupon.setDescription(request.description());
        coupon.setDiscountValue(request.discountValue());
        coupon.setExpirationDate(request.expirationDate());
        coupon.setPublished(request.published());

        Coupon saved = couponRepository.save(coupon);
        return toResponse(saved);
    }

    public List<CouponResponse> getAllCoupons() {
        return couponRepository.findAllActive().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Optional<CouponResponse> getCouponById(UUID id) {
        return couponRepository.findByIdActive(id).map(this::toResponse);
    }

    @Transactional
    public boolean deleteCoupon(UUID id) {
        Optional<Coupon> couponOpt = couponRepository.findByIdActive(id);
        if (couponOpt.isEmpty()) {
            return false; // Not found or already deleted
        }
        int updated = couponRepository.softDeleteById(id, OffsetDateTime.now());
        return updated > 0;
    }

    private CouponResponse toResponse(Coupon coupon) {
        return new CouponResponse(
                coupon.getId().toString(),
                coupon.getCode(),
                coupon.getDescription(),
                coupon.getDiscountValue(),
                coupon.getExpirationDate(),
                coupon.getStatus().name(),
                coupon.isPublished(),
                coupon.isRedeemed()
        );
    }
}