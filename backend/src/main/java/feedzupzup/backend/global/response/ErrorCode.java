package feedzupzup.backend.global.response;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    //Server Error
    SERVER_ERROR(INTERNAL_SERVER_ERROR, "S01", "예상치 못한 서버 에러가 발생하였습니다"),

    //Global Error
    RESOURCE_NOT_FOUNT(NOT_FOUND, "G01", "요청한 자원을 찾을 수 없습니다"),
    NOT_SUPPORTED(BAD_REQUEST, "G02", "지원하지 않는 요청입니다"),

    //Organization Error
    CHEERING_INVALID_NUMBER(BAD_REQUEST, "O01", "응원횟수에 유효하지 않은 숫자값입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
