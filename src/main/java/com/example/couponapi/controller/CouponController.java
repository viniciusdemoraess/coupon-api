package com.example.couponapi.controller;

import com.example.couponapi.dto.CouponCreateRequest;
import com.example.couponapi.dto.CouponResponse;
import com.example.couponapi.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/coupons")
@Tag(name = "Coupons", description = "Coupon management APIs")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    @Operation(summary = "Create a new coupon")
    public ResponseEntity<CouponResponse> createCoupon(@Valid @RequestBody CouponCreateRequest request) {
        try {
            CouponResponse coupon = couponService.createCoupon(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(coupon);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @Operation(summary = "Get all active coupons")
    public ResponseEntity<List<CouponResponse>> getAllCoupons() {
        List<CouponResponse> coupons = couponService.getAllCoupons();
        return ResponseEntity.ok(coupons);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get coupon by ID")
    public ResponseEntity<CouponResponse> getCouponById(@PathVariable UUID id) {
        Optional<CouponResponse> coupon = couponService.getCouponById(id);
        return coupon.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete coupon by ID (soft delete)")
    public ResponseEntity<Void> deleteCoupon(@PathVariable UUID id) {
        boolean deleted = couponService.deleteCoupon(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}