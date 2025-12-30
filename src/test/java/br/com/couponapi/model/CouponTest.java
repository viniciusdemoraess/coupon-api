package br.com.couponapi.model;

import br.com.couponapi.exception.InvalidExpirationDateException;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class CouponTest {
    @Test
    void should_not_allow_coupon_with_past_expiration() {
        assertThrows(InvalidExpirationDateException.class, () ->
            new Coupon(
                "ABC123",
                "Test",
                BigDecimal.ONE,
                OffsetDateTime.now().minusDays(1),
                true
            )
        );
    }
}
