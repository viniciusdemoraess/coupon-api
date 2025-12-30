package br.com.couponapi.model;

import br.com.couponapi.config.TimeConfigTest;
import br.com.couponapi.exception.CouponAlreadyRedeemedException;
import br.com.couponapi.exception.CouponNotPublishedException;
import br.com.couponapi.exception.InvalidDiscountValueException;
import br.com.couponapi.exception.InvalidExpirationDateException;
import br.com.couponapi.model.Coupon.CouponStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@Import(TimeConfigTest.class)
class CouponTest {

   private Clock clock;

    @BeforeEach
    void setUp() {
        clock = Clock.fixed(
            OffsetDateTime.parse("2025-01-01T10:00:00-03:00").toInstant(),
            ZoneId.of("America/Sao_Paulo")
        );
    }

    private OffsetDateTime futureDate() {
        return OffsetDateTime.now(clock).plusDays(5);
    }

    private OffsetDateTime pastDate() {
        return OffsetDateTime.now(clock).minusDays(1);
    }

    @Test
    void should_not_allow_coupon_with_past_expiration() {
        assertThrows(InvalidExpirationDateException.class, () ->
            new Coupon(
                "ABC123",
                "Test",
                BigDecimal.ONE,
                pastDate(),
                true,
                clock
            )
        );
    }

    @Test
    void should_normalize_coupon_code_removing_special_chars() {
        Coupon coupon = new Coupon(
            "AB-12$34",
            "Test",
            BigDecimal.ONE,
            futureDate(),
            true,
            clock
        );

        assertEquals("AB1234", coupon.getCode());
    }

    @Test
    void should_not_allow_discount_less_than_minimum() {
        assertThrows(InvalidDiscountValueException.class, () ->
            new Coupon(
                "ABC123",
                "Test",
                BigDecimal.valueOf(0.4),
                futureDate(),
                true,
                clock
            )
        );
    }

    @Test
    void should_not_allow_consuming_unpublished_coupon() {
        Coupon coupon = new Coupon(
            "ABC123",
            "Test",
            BigDecimal.ONE,
            futureDate(),
            false,
            clock
        );

        assertThrows(CouponNotPublishedException.class,
            () -> coupon.consume(clock)
        );
    }

    @Test
    void should_not_allow_consuming_twice() {
        Coupon coupon = validCoupon();

        coupon.consume(clock);

        assertThrows(CouponAlreadyRedeemedException.class,
            () -> coupon.consume(clock)
        );
    }

    @Test
    void should_mark_coupon_as_deleted() {
        Coupon coupon = validCoupon();

        coupon.softDelete(clock);

        assertEquals(CouponStatus.DELETED, coupon.getStatus(clock));
    }

    private Coupon validCoupon() {
        return new Coupon(
            "ABC123",
            "Test coupon",
            BigDecimal.ONE,
            futureDate(),
            true,
            clock
        );
    }
}
