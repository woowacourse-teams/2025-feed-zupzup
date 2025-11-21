package feedzupzup.backend.global.async;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FailureRetryScheduler {

    private final AsyncTaskFailureRepository asyncTaskFailureRepository;
    private final AsyncTaskFailureService asyncTaskFailureService;

    @Scheduled(fixedDelayString = "${app.async.retry-scheduler.interval:300000}")
    public void retryFailedTasks() {
        List<AsyncTaskFailure> retryableFailures = asyncTaskFailureRepository.findAllByRetryable(true);
        
        if (retryableFailures.isEmpty()) {
            return;
        }
        
        log.info("재시도할 실패 작업 {} 개 발견", retryableFailures.size());

        for (AsyncTaskFailure failure : retryableFailures) {
            asyncTaskFailureService.retry(failure.getId());
        }
    }
}
