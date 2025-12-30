package br.com.couponapi.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    @ExceptionHandler(CouponCodeAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleCouponCodeAlreadyExistsException(CouponCodeAlreadyExistsException ex, WebRequest request) {
        log.warn("Erro de código de cupom já existente: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse("Código do cupom já existe", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CouponNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCouponNotFoundException(CouponNotFoundException ex, WebRequest request) {
        log.warn("Erro de cupom não encontrado: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse("Cupom não encontrado", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({CouponDeletedException.class, CouponAlreadyRedeemedException.class, CouponExpiredException.class, CouponAlreadyDeletedException.class})
    public ResponseEntity<ErrorResponse> handleCouponStateExceptions(RuntimeException ex, WebRequest request) {
        log.warn("Erro de estado do cupom: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse("Operação inválida no cupom", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({InvalidCouponCodeException.class, InvalidDiscountValueException.class, InvalidExpirationDateException.class})
    public ResponseEntity<ErrorResponse> handleValidationExceptions(RuntimeException ex, WebRequest request) {
        log.warn("Erro de validação: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse("Dados inválidos", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        log.error("Erro interno do servidor: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse("Erro interno do servidor", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}