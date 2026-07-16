package com.programmers.kdt.common.exception;

public enum ErrorCode {
    NOT_FOUND("C001", "요청한 리소스를 찾을 수 없습니다."),
    INVALID_REQUEST("C002", "잘못된 요청입니다."),
    EXTERNAL_SERVICE_ERROR("C003", "다른 서비스 호출 중 오류가 발생했습니다.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
}
