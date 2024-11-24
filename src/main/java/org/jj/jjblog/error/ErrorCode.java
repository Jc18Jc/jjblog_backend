package org.jj.jjblog.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    /*
    BAD_REQUEST - 400
    UNAUTHORIZED - 401
    FORBIDDEN - 403
    NOT_FOUND - 404
     */
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "ACCOUNT-1", "아이디 혹은 패스워드가 틀렸습니다."),
    NO_AUTHORIZED(HttpStatus.FORBIDDEN, "AUTHORIZATION-1", "접근 권한이 없습니다."),
    OVER_MAX_PIN(HttpStatus.NOT_FOUND, "PIN-1", "핀 개수를 초과하였습니다."),
    FAIL_REGISTER(HttpStatus.BAD_REQUEST, "POST-1", "입력 형식이 맞지 않습니다."),
    ACCESS_TOKEN_UNACCEPT(HttpStatus.UNAUTHORIZED, "ACCESS-1", "Access 토큰이 없거나 너무 짧습니다."),
    ACCESS_TOKEN_BADTYPE(HttpStatus.UNAUTHORIZED, "ACCESS-2","Access 토큰의 타입이 틀렸습니다."),
    ACCESS_TOKEN_MALFORM(HttpStatus.FORBIDDEN, "ACCESS-3","Access 토큰의 형식이 틀렸습니다."),
    ACCESS_TOKEN_BADSIGN(HttpStatus.FORBIDDEN, "ACCESS-4","서명이 일치하지 않습니다."),
    ACCESS_TOKEN_EXPIRED(HttpStatus.FORBIDDEN, "ACCESS-5","Access 토큰이 만료되었습니다."),
    REFRESH_TOKEN_UNACCEPT(HttpStatus.FORBIDDEN, "REFRESH-1", "Refresh 토큰을 갱신할 수 없습니다."), // malform 혹은 기타 예외
    REFRESH_TOKEN_EXPIRED(HttpStatus.FORBIDDEN, "REFRESH-2", "Refresh 토큰이 만료되었습니다."),
    FILE_BAD(HttpStatus.BAD_REQUEST, "FILE-1", "올바르지 않은 파일입니다."),
    FILE_NOT_DELETE(HttpStatus.BAD_REQUEST, "FILE-2", "파일을 제거할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
