package br.com.couponapi.exception;

public class InvalidDiscountValueException extends RuntimeException {

    public InvalidDiscountValueException(String message) {
        super(message);
    }

    public InvalidDiscountValueException(String message, Throwable cause) {
        super(message, cause);
    }
}