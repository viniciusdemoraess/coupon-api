package br.com.couponapi.model;

import br.com.couponapi.exception.*;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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
            boolean published
    ) {
        this.code = normalizeCode(code);
        validateDiscount(discountValue);
        validateExpiration(expirationDate);

        this.description = description;
        this.discountValue = discountValue;
        this.expirationDate = expirationDate;
        this.published = published;
        this.status = CouponStatus.ACTIVE;
        this.redeemed = false;
    }

    // ====== REGRAS DE NEGÓCIO ======

    public void consume() {
        if (this.deletedAt != null) {
            throw new CouponDeletedException("Cupom foi excluído");
        }

        if (this.redeemed) {
            throw new CouponAlreadyRedeemedException("Cupom já foi resgatado");
        }

        ZoneOffset spOffset = ZoneOffset.of("-03:00");
        OffsetDateTime nowSp = OffsetDateTime.now(spOffset);
        OffsetDateTime expirationSp = this.expirationDate.withOffsetSameInstant(spOffset);
        if (expirationSp.isBefore(nowSp)) {
            this.status = CouponStatus.EXPIRED;
            throw new CouponExpiredException("Cupom expirou");
        }

        this.redeemed = true;
    }

    public void softDelete() {
        if (this.deletedAt != null) {
            throw new CouponAlreadyDeletedException("Cupom já foi excluído");
        }
        this.deletedAt = OffsetDateTime.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    // ====== MÉTODOS PRIVADOS ======

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

    private void validateExpiration(OffsetDateTime date) {
        ZoneOffset spOffset = ZoneOffset.of("-03:00");
        OffsetDateTime nowSp = OffsetDateTime.now(spOffset);
        OffsetDateTime expirationSp = date.withOffsetSameInstant(spOffset);
        // Permite datas até 1 minuto no passado para lidar com latência de rede
        if (expirationSp.isBefore(nowSp.minusMinutes(1))) {
            throw new InvalidExpirationDateException("Data de expiração não pode ser no passado");
        }
    }

    public enum CouponStatus {
        ACTIVE, INACTIVE, EXPIRED
    }
}
