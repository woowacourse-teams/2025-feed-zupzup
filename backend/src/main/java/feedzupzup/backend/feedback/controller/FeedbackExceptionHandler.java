package feedzupzup.backend.feedback.controller;

import feedzupzup.backend.feedback.exception.FeedbackException;
import feedzupzup.backend.feedback.exception.FeedbackException.FeedbackNegativeException;
import feedzupzup.backend.global.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class FeedbackExceptionHandler {

    @ExceptionHandler(FeedbackNegativeException.class)
    public ResponseEntity<ErrorResponse> handleFeedbackException(final FeedbackException e) {
        log.error(e.getMessage(), e);
        final HttpStatus httpStatus = e.getErrorCode().getHttpStatus();
        return ResponseEntity.status(httpStatus)
                .body(ErrorResponse.error(e.getErrorCode()));
    }
}
