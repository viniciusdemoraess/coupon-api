package br.com.couponapi.exception;

public class CouponDeletedException extends RuntimeException {

    public CouponDeletedException(String message) {
        super(message);
    }

    public CouponDeletedException(String message, Throwable cause) {
        super(message, cause);
    }
}