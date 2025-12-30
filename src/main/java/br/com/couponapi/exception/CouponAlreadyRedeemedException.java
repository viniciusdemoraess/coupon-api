package br.com.couponapi.exception;

public class CouponAlreadyRedeemedException extends RuntimeException {

    public CouponAlreadyRedeemedException(String message) {
        super(message);
    }

    public CouponAlreadyRedeemedException(String message, Throwable cause) {
        super(message, cause);
    }
}