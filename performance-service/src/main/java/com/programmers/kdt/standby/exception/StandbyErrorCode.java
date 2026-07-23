package com.programmers.kdt.standby.exception;
import com.programmers.kdt.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum StandbyErrorCode implements ErrorCode{
    INVALID_ZONE_COUNT(HttpStatus.BAD_REQUEST, "STB400_001", "지망 구역은 1개 이상 3개 이하로 신청할 수 있습니다."),
    DUPLICATE_ZONE(HttpStatus.BAD_REQUEST, "STB400_002", "지망 구역은 중복될 수 없습니다."),
    INVALID_ZONE(HttpStatus.BAD_REQUEST, "STB400_003", "해당 공연에서 사용하지 않는 좌석 구역입니다."),
    ALREADY_APPLIED(HttpStatus.CONFLICT, "STB409_001", "이미 해당 회차에 대기 신청을 하셨습니다."),
    ZONE_NOT_SOLD_OUT(HttpStatus.CONFLICT, "STB409_002", "아직 잔여 좌석이 있어 대기 신청할 수 없는 구역입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    StandbyErrorCode(HttpStatus httpStatus, String code, String message) {
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
