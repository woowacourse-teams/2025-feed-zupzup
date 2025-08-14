package feedzupzup.backend.notification.infrastructure;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import feedzupzup.backend.notification.exception.NotificationException;
import feedzupzup.backend.notification.exception.NotificationException.RetryInterruptedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class FcmRetryHandler {

    private static final int MAX_RETRY_COUNT = 4;
    private static final int BASE_DELAY_SECONDS = 1;
    private static final double BACKOFF_FACTOR = 0.5;
    private static final int MIN_TIMEOUT_SECONDS = 10;
    private static final Random random = new Random();

    public boolean shouldRetry(Exception exception, int attemptCount) {
        if (attemptCount >= MAX_RETRY_COUNT) {
            log.warn("최대 재시도 횟수({})에 도달했습니다.", MAX_RETRY_COUNT);
            return false;
        }

        if (!(exception instanceof FirebaseMessagingException)) {
            log.warn("FirebaseMessagingException이 아닌 예외는 재시도하지 않습니다: {}", exception.getClass().getSimpleName());
            return false;
        }

        FirebaseMessagingException fmException = (FirebaseMessagingException) exception;
        MessagingErrorCode errorCode = fmException.getMessagingErrorCode();

        return switch (errorCode) {
            case UNAVAILABLE, INTERNAL -> {
                log.info("재시도 가능한 에러 코드: {}", errorCode);
                yield true;
            }
            case QUOTA_EXCEEDED -> {
                log.warn("할당량 초과 에러 - 재시도합니다: {}", errorCode);
                yield true;
            }
            case INVALID_ARGUMENT, SENDER_ID_MISMATCH, UNREGISTERED -> {
                log.error("재시도하지 않는 클라이언트 에러: {}", errorCode);
                yield false;
            }
            default -> {
                log.warn("알 수 없는 에러 코드로 재시도하지 않습니다: {}", errorCode);
                yield false;
            }
        };
    }

    public void waitBeforeRetry(int attemptCount) {
        try {
            long delayMillis = calculateDelay(attemptCount);
            log.info("{}번째 재시도 전 {}ms 대기", attemptCount + 1, delayMillis);
            Thread.sleep(delayMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RetryInterruptedException("재시도 대기 중 인터럽트 발생");
        }
    }

    private long calculateDelay(int attemptCount) {
        // 지수 백오프: 1s, 2s, 4s, 8s... with jitter
        long baseDelay = (long) (BASE_DELAY_SECONDS * Math.pow(2, attemptCount * BACKOFF_FACTOR));
        
        // 최소 10초 타임아웃 보장
        long delay = Math.max(baseDelay, MIN_TIMEOUT_SECONDS);
        
        // Jitter 적용 (±20% 범위)
        double jitterFactor = 0.8 + (random.nextDouble() * 0.4); // 0.8 ~ 1.2
        
        return (long) (TimeUnit.SECONDS.toMillis(delay) * jitterFactor);
    }

    public void logRetryAttempt(int attemptCount, Exception exception) {
        log.warn("FCM 전송 실패 (시도 {}/{}): {}", 
                attemptCount + 1, MAX_RETRY_COUNT, exception.getMessage());
    }

    public void logFinalFailure(Exception exception) {
        log.error("FCM 전송 최종 실패 - 모든 재시도 완료: {}", exception.getMessage());
    }
}
