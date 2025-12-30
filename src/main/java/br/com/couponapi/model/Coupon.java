package br.com.couponapi.model;

import br.com.couponapi.exception.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 6)
    private String code;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal discountValue;

    @Column(nullable = false)
    private OffsetDateTime expirationDate;

    @Column(nullable = false)
    private boolean published;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponStatus status;

    @Column(nullable = false)
    private boolean redeemed;

    private OffsetDateTime deletedAt;

    public Coupon(
            String code,
            String description,
            BigDecimal discountValue,
            OffsetDateTime expirationDate,
            boolean published,
            Clock clock
    ) {
        this.code = normalizeCode(code);
        validateDiscount(discountValue);
        validateExpiration(expirationDate, clock);

        this.description = description;
        this.discountValue = discountValue;
        this.expirationDate = expirationDate;
        this.published = published;
        this.status = CouponStatus.ACTIVE;
        this.redeemed = false;
    }

    public void consume(Clock clock) {
        if (this.deletedAt != null) {
            throw new CouponDeletedException("Cupom foi excluído");
        }

        if (!this.published) {
            throw new CouponNotPublishedException("Cupom não está publicado");
        }

        if (this.redeemed) {
            throw new CouponAlreadyRedeemedException("Cupom já foi resgatado");
        }

        if (this.expirationDate.isBefore(OffsetDateTime.now(clock))) {
            throw new CouponExpiredException("Cupom expirou");
        }

        this.redeemed = true;
        this.status = CouponStatus.INACTIVE;
    }

    public void softDelete(Clock clock) {
        if (this.deletedAt != null) {
            throw new CouponAlreadyDeletedException("Cupom já foi excluído");
        }
        this.status = CouponStatus.DELETED;
        this.deletedAt = OffsetDateTime.now(clock);
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }


    private String normalizeCode(String code) {
        String cleaned = code.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        if (cleaned.length() != 6) {
            throw new InvalidCouponCodeException("Código do cupom deve ter exatamente 6 caracteres");
        }
        return cleaned;
    }

    private void validateDiscount(BigDecimal value) {
        if (value.compareTo(BigDecimal.valueOf(0.5)) < 0) {
            throw new InvalidDiscountValueException("Valor do desconto deve ser pelo menos 0.5");
        }
    }

    private void validateExpiration(OffsetDateTime date, Clock clock) {
        OffsetDateTime now = OffsetDateTime.now(clock);

        if (date.isBefore(now.minusMinutes(1))) {
            throw new InvalidExpirationDateException("Data de expiração não pode ser no passado");
        }
    }

    public CouponStatus getStatus(Clock clock) {
        if (deletedAt != null) return CouponStatus.DELETED;
        if (status == CouponStatus.INACTIVE) return CouponStatus.INACTIVE;
        if (isExpired(clock)) return CouponStatus.INACTIVE;
        return CouponStatus.ACTIVE;
    }

    public boolean isExpired(Clock clock) {
        return expirationDate.isBefore(OffsetDateTime.now(clock));
    }


    public enum CouponStatus {
        ACTIVE, INACTIVE, DELETED
    }
}
