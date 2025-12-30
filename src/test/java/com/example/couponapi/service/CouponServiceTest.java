package com.example.couponapi.service;

import com.example.couponapi.dto.CouponCreateRequest;
import com.example.couponapi.dto.CouponResponse;
import com.example.couponapi.model.Coupon;
import com.example.couponapi.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCoupon_ValidRequest_ShouldCreateCoupon() {
        // Arrange
        CouponCreateRequest request = new CouponCreateRequest("ABC123", "Test Coupon", BigDecimal.valueOf(10.0), OffsetDateTime.now().plusDays(1), true);

        Coupon savedCoupon = new Coupon();
        savedCoupon.setId(UUID.randomUUID());
        savedCoupon.setCode("ABC123");
        savedCoupon.setDescription("Test Coupon");
        savedCoupon.setDiscountValue(BigDecimal.valueOf(10.0));
        savedCoupon.setExpirationDate(request.expirationDate());
        savedCoupon.setPublished(true);

        when(couponRepository.findByCodeActive(anyString())).thenReturn(Optional.empty());
        when(couponRepository.save(any(Coupon.class))).thenReturn(savedCoupon);

        // Act
        CouponResponse result = couponService.createCoupon(request);

        // Assert
        assertNotNull(result);
        assertEquals("ABC123", result.code());
        verify(couponRepository).save(any(Coupon.class));
    }

    @Test
    void createCoupon_CodeWithSpecialChars_ShouldProcessCode() {
        // Arrange
        CouponCreateRequest request = new CouponCreateRequest("A@B#C$123!", "Test Coupon", BigDecimal.valueOf(10.0), OffsetDateTime.now().plusDays(1), false);

        Coupon savedCoupon = new Coupon();
        savedCoupon.setId(UUID.randomUUID());
        savedCoupon.setCode("ABC123");
        savedCoupon.setDescription("Test Coupon");
        savedCoupon.setDiscountValue(BigDecimal.valueOf(10.0));
        savedCoupon.setExpirationDate(request.expirationDate());
        savedCoupon.setPublished(false);

        when(couponRepository.findByCodeActive("ABC123")).thenReturn(Optional.empty());
        when(couponRepository.save(any(Coupon.class))).thenReturn(savedCoupon);

        // Act
        CouponResponse result = couponService.createCoupon(request);

        // Assert
        assertEquals("ABC123", result.code());
        verify(couponRepository).save(any(Coupon.class));
    }

    @Test
    void createCoupon_ExpirationDateInPast_ShouldThrowException() {
        // Arrange
        CouponCreateRequest request = new CouponCreateRequest("ABC123", "Test Coupon", BigDecimal.valueOf(10.0), OffsetDateTime.now().minusDays(1), false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> couponService.createCoupon(request));
        assertEquals("Expiration date must be in the future", exception.getMessage());
    }

    @Test
    void createCoupon_CodeAlreadyExists_ShouldThrowException() {
        // Arrange
        CouponCreateRequest request = new CouponCreateRequest("ABC123", "Test Coupon", BigDecimal.valueOf(10.0), OffsetDateTime.now().plusDays(1), false);

        when(couponRepository.findByCodeActive("ABC123")).thenReturn(Optional.of(new Coupon()));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> couponService.createCoupon(request));
        assertEquals("Coupon code already exists", exception.getMessage());
    }

    @Test
    void deleteCoupon_ExistingCoupon_ShouldSoftDelete() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(couponRepository.findByIdActive(id)).thenReturn(Optional.of(new Coupon()));
        when(couponRepository.softDeleteById(eq(id), any(OffsetDateTime.class))).thenReturn(1);

        // Act
        boolean result = couponService.deleteCoupon(id);

        // Assert
        assertTrue(result);
        verify(couponRepository).softDeleteById(eq(id), any(OffsetDateTime.class));
    }

    @Test
    void createCoupon_DiscountValueBelowMinimum_ShouldThrowException() {
        // Arrange
        CouponCreateRequest request = new CouponCreateRequest("ABC123", "Test Coupon", BigDecimal.valueOf(0.4), OffsetDateTime.now().plusDays(1), false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> couponService.createCoupon(request));
        assertEquals("Discount value must be at least 0.5", exception.getMessage());
    }

    @Test
    void deleteCoupon_NonExistingCoupon_ShouldReturnFalse() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(couponRepository.findByIdActive(id)).thenReturn(Optional.empty());

        // Act
        boolean result = couponService.deleteCoupon(id);

        // Assert
        assertFalse(result);
        verify(couponRepository, never()).softDeleteById(any(UUID.class), any(OffsetDateTime.class));
    }
}