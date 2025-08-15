package feedzupzup.backend.global.response;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

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
    INVALID_ORGANIZATION_LENGTH(BAD_REQUEST, "O02", "조직 이름은 1글자 이상 10글자 이하여야 합니다."),
    
    //Feedback Error
    INVALID_USERNAME_LENGTH(BAD_REQUEST, "F01", "닉네임은 1글자 이상 10글자 이하여야 합니다."),
    INVALID_CONTENT_LENGTH(BAD_REQUEST, "F02", "내용은 1글자 이상 500글자 이하여야 합니다."),
    
    //Admin Domain Error
    INVALID_ADMIN_ID_FORMAT(BAD_REQUEST, "A01", "관리자 ID는 공백을 포함할 수 없습니다."),
    INVALID_PASSWORD_FORMAT(BAD_REQUEST, "A02", "비밀번호는 공백을 포함하지 않고 5글자 이상이어야 합니다."),
    INVALID_ADMIN_NAME_FORMAT(BAD_REQUEST, "A03", "관리자 이름은 공백을 포함할 수 없습니다."),
    
    //Auth Error
    ADMIN_NOT_LOGGED_IN(UNAUTHORIZED, "A04", "로그인이 필요합니다."),
    DUPLICATE_LOGIN_ID(BAD_REQUEST, "A05", "이미 존재하는 로그인 ID입니다."),
    INVALID_LOGIN_CREDENTIALS(BAD_REQUEST, "A06", "로그인 ID 또는 비밀번호가 올바르지 않습니다."),
    PASSWORD_NOT_MATCH(BAD_REQUEST, "A07", "비밀번호와 비밀번호 확인이 일치하지 않습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
