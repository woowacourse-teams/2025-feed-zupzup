package feedzupzup.backend.s3.controller;

import feedzupzup.backend.global.response.ErrorResponse;
import feedzupzup.backend.s3.exception.S3PresignedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class S3ExceptionHandler {

    @ExceptionHandler(S3PresignedException.class)
    public ResponseEntity<ErrorResponse> handleS3PresignedException(final S3PresignedException e) {
        log.error(e.getMessage(), e);
        final HttpStatus httpStatus = e.getErrorCode().getHttpStatus();
        return ResponseEntity.status(httpStatus)
                .body(ErrorResponse.error(e.getErrorCode()));
    }
}
