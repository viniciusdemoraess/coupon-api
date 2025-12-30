package br.com.couponapi.exception;


public class CouponNotPublishedException extends RuntimeException {

    public CouponNotPublishedException(String message) {
        super(message);
    }

    public CouponNotPublishedException(String message, Throwable cause) {
        super(message, cause);
    }
}