package feedzupzup.backend.qr.controller;

import feedzupzup.backend.global.response.ErrorResponse;
import feedzupzup.backend.qr.infrastructure.exception.QRGenerationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class QRExceptionHandler {

    @ExceptionHandler(QRGenerationException.class)
    public ResponseEntity<ErrorResponse> handleQRGenerationException(final QRGenerationException e) {
        log.warn(e.getMessage(), e);
        final HttpStatus httpStatus = e.getErrorCode().getHttpStatus();
        return ResponseEntity.status(httpStatus)
                .body(ErrorResponse.error(e.getErrorCode()));
    }
}
