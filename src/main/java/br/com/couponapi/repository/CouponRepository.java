package br.com.couponapi.repository;

import br.com.couponapi.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CouponRepository extends JpaRepository<Coupon, UUID> {

    Optional<Coupon> findByCodeAndDeletedAtIsNull(String code);

    Optional<Coupon> findByIdAndDeletedAtIsNull(UUID id);

    List<Coupon> findAllByDeletedAtIsNull();

}
