package feedzupzup.backend.global.exception;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import feedzupzup.backend.global.response.ErrorCode;
import feedzupzup.backend.global.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //TODO : 로깅 추가
    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse<Void> handleException(Exception e) {
        ErrorCode errorCode = ErrorCode.SERVER_ERROR;
        return ErrorResponse.error(errorCode, errorCode.getMessage());
    }

    @ExceptionHandler(ResourceException.class)
    public ResponseEntity<ErrorResponse<Void>> handleException(ResourceException e) {
        HttpStatus httpStatus = e.getErrorCode().getHttpStatus();
        return ResponseEntity.status(httpStatus)
                .body(ErrorResponse.error(e.getErrorCode(), e.getMessage()));
    }
}
