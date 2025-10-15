package feedzupzup.backend.global.response;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
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
    RESOURCE_NOT_FOUND(NOT_FOUND, "G01", "요청한 자원을 찾을 수 없습니다"),
    NOT_SUPPORTED(BAD_REQUEST, "G02", "지원하지 않는 요청입니다"),
    RESOURCE_EXISTS(BAD_REQUEST, "G03", "이미 존재하는 자원입니다."),
    INVALID_AUTHORIZE(UNAUTHORIZED, "G04", "접근 권한이 존재하지 않습니다."),
    REST_CLIENT_SERVER_FAIL(INTERNAL_SERVER_ERROR, "G05", "외부 API 처리 작업을 실패하였습니다."),

    //Organization Error
    CHEERING_INVALID_NUMBER(BAD_REQUEST, "O01", "응원횟수에 유효하지 않은 숫자값입니다."),
    INVALID_ORGANIZATION_LENGTH(BAD_REQUEST, "O02", "조직 이름은 1글자 이상 10글자 이하여야 합니다."),

    //Feedback Error
    INVALID_USERNAME_LENGTH(BAD_REQUEST, "F01", "닉네임은 1글자 이상 10글자 이하여야 합니다."),
    INVALID_CONTENT_LENGTH(BAD_REQUEST, "F02", "내용은 1글자 이상 500글자 이하여야 합니다."),
    INVALID_VALUE_RANGE(INTERNAL_SERVER_ERROR, "F03", "좋아요는 음수가 될 수 없습니다."),
    LIKE_ALREADY_EXISTS(BAD_REQUEST, "F04", "좋아요는 한 번만 누를 수 있습니다."),
    INVALID_LIKE_REQUEST(BAD_REQUEST, "F05", "잘못된 요청입니다."),

    //Admin Domain Error
    INVALID_ADMIN_ID_FORMAT(BAD_REQUEST, "A01", "관리자 ID는 공백을 포함할 수 없습니다."),
    INVALID_PASSWORD_FORMAT(BAD_REQUEST, "A02", "비밀번호는 공백을 포함하지 않고 5글자 이상이어야 합니다."),
    INVALID_ADMIN_NAME_FORMAT(BAD_REQUEST, "A03", "관리자 이름은 공백을 포함할 수 없습니다."),

    //Auth Error
    ADMIN_NOT_LOGGED_IN(UNAUTHORIZED, "A04", "로그인이 필요합니다."),
    DUPLICATE_LOGIN_ID(BAD_REQUEST, "A05", "이미 존재하는 로그인 ID입니다."),
    INVALID_LOGIN_CREDENTIALS(BAD_REQUEST, "A06", "로그인 ID 또는 비밀번호가 올바르지 않습니다."),
    PASSWORD_NOT_MATCH(BAD_REQUEST, "A07", "비밀번호와 비밀번호 확인이 일치하지 않습니다."),
    ACCESS_FORBIDDEN(FORBIDDEN, "A08", "해당 기능에 대한 권한이 없습니다."),

    //Notification Error
    NOTIFICATION_RETRY_INTERRUPTED(INTERNAL_SERVER_ERROR, "N01", "알림 재시도 중 인터럽트가 발생했습니다."),
    FIREBASE_INITIALIZATION_FAILED(INTERNAL_SERVER_ERROR, "N02", "Firebase를 초기화할 수 없습니다."),
    NOTIFICATION_TOKEN_ALREADY_EXISTS(BAD_REQUEST, "N03", "이미 등록된 알림 토큰이 있습니다."),

    //QR Error
    QR_GENERATION_FAILED(INTERNAL_SERVER_ERROR, "Q01", "QR 생성에 실패하였습니다."),

    //S3 Error
    S3_UPLOAD_FAILED(INTERNAL_SERVER_ERROR, "S01", "파일 업로드에 실패하였습니다."),
    S3_PRESIGNED_FAILED(INTERNAL_SERVER_ERROR, "S02", "Presigned URL 생성에 실패하였습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
