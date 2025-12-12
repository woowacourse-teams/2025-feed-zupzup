package feedzupzup.backend.feedback.application.retry;

import com.github.sonus21.rqueue.core.RqueueMessage;
import com.github.sonus21.rqueue.utils.backoff.TaskExecutionBackOff;
import java.util.Random;

/**
 * 랜덤 지수 백오프 전략
 *
 * 각 재시도마다 랜덤한 대기 시간 적용:
 * - 1차 재시도: 0~1초
 * - 2차 재시도: 1~2초
 * - 3차 재시도: 2~4초
 * - 4차 재시도: 4~8초
 * - 5차 재시도: 8~16초
 */
public class RandomExponentialBackOff implements TaskExecutionBackOff {

    private static final long[] MIN_DELAYS = {0L, 1000L, 2000L, 4000L, 8000L};
    private static final long[] MAX_DELAYS = {1000L, 2000L, 4000L, 8000L, 16000L};
    private static final Random random = new Random();

    @Override
    public long nextBackOff(Object queue, RqueueMessage message, int failureCount) {
        if (failureCount >= MIN_DELAYS.length) {
            return STOP;
        }
        long minDelay = MIN_DELAYS[failureCount];
        long maxDelay = MAX_DELAYS[failureCount];

        // minDelay와 maxDelay 사이의 랜덤한 값 반환
        return random.nextLong(minDelay, maxDelay + 1);
    }
}
