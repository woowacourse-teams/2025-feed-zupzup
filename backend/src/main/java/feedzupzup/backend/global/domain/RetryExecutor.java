package feedzupzup.backend.global.domain;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RetryExecutor {

    public static void execute(
            final ThrowingSupplier<Boolean> action,
            final int maxRetries,
            final long delayMs,
            final String taskName
    ) {
        for (int i = 1; i <= maxRetries; i++) {
            try {
                boolean isSuccess = action.get();
                if (isSuccess) {
                    log.info(taskName + " 작업 성공");
                }
            } catch (Exception e) {
                log.warn("[{}] 에러 발생으로 인한 재시도 (시도: {}/{}) - {}", taskName, i, maxRetries,
                        e.getMessage());
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        log.error("[{}] 최대 재시도 횟수({}) 초과. 실패.", taskName, maxRetries);
    }

}
