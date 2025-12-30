package br.com.couponapi.service;

import br.com.couponapi.dtos.CouponCreateRequest;
import br.com.couponapi.dtos.CouponResponse;
import br.com.couponapi.exception.CouponCodeAlreadyExistsException;
import br.com.couponapi.exception.CouponNotFoundException;
import br.com.couponapi.model.Coupon;
import br.com.couponapi.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
@Log4j2
public class CouponService {

    private final CouponRepository repository;

    private final Clock clock;

    @Transactional
    public CouponResponse create(CouponCreateRequest request) {
        log.info("Criando cupom com código: {}", request.code());
        repository.findByCodeAndDeletedAtIsNull(
                request.code().replaceAll("[^a-zA-Z0-9]", "").toUpperCase()
        ).ifPresent(c -> {
            log.warn("Código do cupom já existe: {}", request.code());
            throw new CouponCodeAlreadyExistsException("Código do cupom já existe: " + request.code());
        });

        Coupon coupon = new Coupon(
                request.code(),
                request.description(),
                request.discountValue(),
                request.expirationDate(),
                request.published(),
                clock
        );

        return toResponse(repository.save(coupon));
    }

    public List<CouponResponse> getAll() {
        log.info("Recuperando todos os cupons ativos");
        return repository.findAllByDeletedAtIsNull()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CouponResponse consume(UUID id) {
        log.info("Consumindo cupom com id: {}", id);
        Coupon coupon = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> {
                    log.warn("Cupom não encontrado com id: {}", id);
                    return new CouponNotFoundException("Cupom não encontrado com id: " + id);
                });

        coupon.consume(clock);
        return toResponse(repository.save(coupon));
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Excluindo logicamente cupom com id: {}", id);
        Coupon coupon = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> {
                    log.warn("Cupom não encontrado com id: {}", id);
                    return new CouponNotFoundException("Cupom não encontrado com id: " + id);
                });

        coupon.softDelete(clock);
        repository.save(coupon);
    }

    private CouponResponse toResponse(Coupon coupon) {
        return new CouponResponse(
                coupon.getId().toString(),
                coupon.getCode(),
                coupon.getDescription(),
                coupon.getDiscountValue(),
                coupon.getExpirationDate(),
                coupon.getStatus(clock).name(),
                coupon.isPublished(),
                coupon.isRedeemed()
        );
    }

    public CouponResponse getById(UUID id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new CouponNotFoundException("Cupom não encontrado com id: " + id));
    }

}
