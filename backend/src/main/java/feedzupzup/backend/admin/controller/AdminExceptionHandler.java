package feedzupzup.backend.admin.controller;

import feedzupzup.backend.admin.domain.exception.AdminException;
import feedzupzup.backend.global.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class AdminExceptionHandler {

    @ExceptionHandler(AdminException.class)
    public ResponseEntity<ErrorResponse> handleOrganizationException(AdminException e) {
        log.warn(e.getMessage(), e);
        final HttpStatus httpStatus = e.getErrorCode().getHttpStatus();
        return ResponseEntity.status(httpStatus)
                .body(ErrorResponse.error(e.getErrorCode()));
    }
}
