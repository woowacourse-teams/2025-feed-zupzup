package feedzupzup.backend.global.exception;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import feedzupzup.backend.global.response.ErrorCode;
import feedzupzup.backend.global.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    //TODO : 로깅 추가
    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse<Void> handleResourceException(final Exception e) {
        final ErrorCode errorCode = ErrorCode.SERVER_ERROR;
        log.error(errorCode.getMessage(), e);
        return ErrorResponse.error(errorCode);
    }

    @ExceptionHandler(ResourceException.class)
    public ResponseEntity<ErrorResponse<Void>> handleResourceException(final ResourceException e) {
        log.warn(e.getMessage(), e);
        return ResponseEntity.status(e.getHttpStatus())
                .body(ErrorResponse.error(e.getErrorCode()));
    }

    @ExceptionHandler(BusinessViolationException.class)
    public ResponseEntity<ErrorResponse<Void>> handleBusinessViolationException(final BusinessViolationException e) {
        log.warn(e.getMessage(), e);
        return ResponseEntity.status(e.getHttpStatus())
                .body(ErrorResponse.error(e.getErrorCode()));
    }
}
