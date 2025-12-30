package br.com.couponapi.services;

import br.com.couponapi.dtos.CouponCreateRequest;
import br.com.couponapi.dtos.CouponResponse;
import br.com.couponapi.exception.CouponAlreadyRedeemedException;
import br.com.couponapi.exception.CouponCodeAlreadyExistsException;
import br.com.couponapi.exception.CouponNotFoundException;
import br.com.couponapi.exception.CouponNotPublishedException;
import br.com.couponapi.exception.InvalidDiscountValueException;
import br.com.couponapi.exception.InvalidExpirationDateException;
import br.com.couponapi.model.Coupon;
import br.com.couponapi.service.CouponService;
import br.com.couponapi.config.TimeConfigTest;


import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Import(TimeConfigTest.class)
class CouponServiceTest {

    @Autowired
    CouponService service;

    @Autowired
    Clock clock;

    private OffsetDateTime futureDate() {
        return OffsetDateTime.now(clock).plusDays(5);
    }

    private OffsetDateTime pastDate() {
        return OffsetDateTime.now(clock).minusDays(1);
    }

    @Test
    void should_create_coupon_successfully() {
        CouponCreateRequest request = validRequest("AB-12$34");

        CouponResponse response = service.create(request);

        assertEquals("AB1234", response.code());
        assertFalse(response.redeemed());
        assertEquals("ACTIVE", response.status());
    }

    @Test
    void should_not_allow_duplicate_code() {
        CouponCreateRequest request = validRequest("ABC123");

        service.create(request);

        assertThrows(CouponCodeAlreadyExistsException.class,
                () -> service.create(request));
    }

    @Test
    void should_not_allow_discount_less_than_minimum() {
        CouponCreateRequest request = new CouponCreateRequest(
                "ABC123",
                "Test",
                BigDecimal.valueOf(0.2),
                futureDate(),
                true
        );

        assertThrows(InvalidDiscountValueException.class,
                () -> service.create(request));
    }

    @Test
    void should_not_allow_past_expiration() {
        CouponCreateRequest request = new CouponCreateRequest(
                "ABC123",
                "Test",
                BigDecimal.ONE,
                pastDate(),
                true
        );

        assertThrows(InvalidExpirationDateException.class,
                () -> service.create(request));
    }

    @Test
    void should_consume_coupon_successfully() {
        CouponResponse created = service.create(validRequest("XYZ999"));

        CouponResponse consumed = service.consume(UUID.fromString(created.id()));

        assertTrue(consumed.redeemed());
    }

    @Test
    void should_not_allow_consuming_twice() {
        CouponResponse created = service.create(validRequest("QWE123"));
        UUID id = UUID.fromString(created.id());

        service.consume(id);

        assertThrows(CouponAlreadyRedeemedException.class,
                () -> service.consume(id));
    }

    @Test
    void should_not_allow_consuming_unpublished_coupon() {
        Coupon coupon = new Coupon(
            "ABC123",
            "Teste",
            BigDecimal.ONE,
            OffsetDateTime.now(clock).plusDays(1),
            false,
            clock
        );

        assertThrows(CouponNotPublishedException.class,
            () -> coupon.consume(clock)
        );
    }


        @Test
        void should_soft_delete_coupon() {
        CouponResponse created = service.create(validRequest("DEL123"));
        UUID id = UUID.fromString(created.id());

        service.delete(id);

        assertThrows(CouponNotFoundException.class,
                () -> service.consume(id));
    }

    @Test
    void get_all_should_return_only_active_coupons() {
        service.create(validRequest("AAA111"));
        service.create(validRequest("BBB222"));

        List<CouponResponse> coupons = service.getAll();

        assertEquals(2, coupons.size());
    }

    private CouponCreateRequest validRequest(String code) {
        return new CouponCreateRequest(
                code,
                "Test coupon",
                BigDecimal.ONE,
                futureDate(),
                true
        );
    }
}
