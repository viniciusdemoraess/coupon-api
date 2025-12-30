package com.example.couponapi.repository;

import com.example.couponapi.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, UUID> {

    @Query("SELECT c FROM Coupon c WHERE c.deletedAt IS NULL")
    List<Coupon> findAllActive();

    @Query("SELECT c FROM Coupon c WHERE c.id = :id AND c.deletedAt IS NULL")
    Optional<Coupon> findByIdActive(@Param("id") UUID id);

    @Query("SELECT c FROM Coupon c WHERE c.code = :code AND c.deletedAt IS NULL")
    Optional<Coupon> findByCodeActive(@Param("code") String code);

    @Modifying
    @Query("UPDATE Coupon c SET c.deletedAt = :deletedAt WHERE c.id = :id AND c.deletedAt IS NULL")
    int softDeleteById(@Param("id") UUID id, @Param("deletedAt") OffsetDateTime deletedAt);
}