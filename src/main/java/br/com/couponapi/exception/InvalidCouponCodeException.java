package br.com.couponapi.exception;

public class InvalidCouponCodeException extends RuntimeException {

    public InvalidCouponCodeException(String message) {
        super(message);
    }

    public InvalidCouponCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}