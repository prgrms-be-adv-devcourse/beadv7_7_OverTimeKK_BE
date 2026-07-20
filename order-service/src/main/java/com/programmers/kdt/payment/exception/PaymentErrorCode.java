package com.programmers.kdt.payment.exception;

import com.programmers.kdt.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum PaymentErrorCode implements ErrorCode {

    PAYMENT_KEY_MISMATCH(HttpStatus.BAD_REQUEST, "P_400_1", "요청한 결제 정보가 일치하지 않습니다."),
    INVALID_PAYMENT_AMOUNT(HttpStatus.BAD_REQUEST, "P_400_2", "결제 금액이 유효하지 않습니다."),

    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "P_404_1", "결제 내역을 찾을 수 없습니다."),

    PAYMENT_ALREADY_EXISTS(HttpStatus.CONFLICT, "P_409_1", "이미 해당 주문에 대한 결제가 존재합니다."),
    INVALID_PAYMENT_STATUS(HttpStatus.CONFLICT, "P_409_2", "현재 결제 상태에서는 처리할 수 없는 요청입니다."),

    PG_REQUEST_FAILED(HttpStatus.BAD_GATEWAY, "P_502_1", "PG사 요청 처리 중 오류가 발생했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    PaymentErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}