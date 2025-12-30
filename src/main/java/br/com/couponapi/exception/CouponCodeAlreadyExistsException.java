package br.com.couponapi.exception;

public class CouponCodeAlreadyExistsException extends RuntimeException {

    public CouponCodeAlreadyExistsException(String message) {
        super(message);
    }

    public CouponCodeAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}