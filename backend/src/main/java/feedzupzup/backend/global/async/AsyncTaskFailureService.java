package feedzupzup.backend.global.async;

import feedzupzup.backend.feedback.application.FeedbackClusteringService;
import feedzupzup.backend.global.async.exception.RetryableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AsyncTaskFailureService {

    private final AsyncTaskFailureRepository asyncTaskFailureRepository;
    private final AsyncFailureAlertService asyncFailureAlertService;
    private final FeedbackClusteringService feedbackClusteringService;

    @Transactional
    public Long recordFailure(
            final TaskType taskType,
            final TargetType targetType,
            final String targetId,
            final Exception exception
    ) {
        String errorMessage = exception.getMessage();
        boolean retryable = isRetryable(exception);
        AsyncTaskFailure failure = AsyncTaskFailure.create(taskType, targetType, targetId, errorMessage, retryable);

        log.warn("비동기 작업 실패 기록 - 작업: {}, 대상 타입 : {}, 대상ID: {}, 에러 클래스: {}",
                taskType, targetType, targetId, exception.getClass().getSimpleName());
        return asyncTaskFailureRepository.save(failure).getId();
    }

    private boolean isRetryable(final Exception exception) {
        return exception instanceof RetryableException;
    }

    @Transactional
    public void retry(final Long asyncTaskFailureId) {
        AsyncTaskFailure failure = getById(asyncTaskFailureId);
        try {
            failure.incrementRetryCount();
            log.error("현재 실패 작업 재시도 현황 - 작업: {}, 대상ID: {}, 재시도 횟수: {}, 재시도 가능여부 {}",
                    failure.getTaskType(), failure.getTargetId(), failure.getRetryCount(), failure.isRetryable());
            retryTask(failure);
        } catch (Exception e) {
            log.error("재시도 작업 실행 중 오류 발생: 작업={}, 대상ID={}", failure.getTaskType(), failure.getTargetId(), e);
            if (failure.isMaxRetryCount() || failure.isFinalFailed()) {
                failure.finalFailed();
                alertFinalFail(failure.getId());
            }
        }
    }

    public void alertFinalFail(final Long asyncTaskFailureId) {
        AsyncTaskFailure failure = getById(asyncTaskFailureId);
        if (!failure.isFinalFailed()) {
            log.info("finalFail만 알람을 보낼 수 있습니다.");
            return;
        }
        asyncFailureAlertService.alert(asyncTaskFailureId);
    }

    private void retryTask(final AsyncTaskFailure failure) {
        TaskType taskType = failure.getTaskType();
        String targetId = failure.getTargetId();

        log.info("작업 재시도 시작: 작업={}, 대상ID={}, 재시도 횟수={}", taskType, targetId, failure.getRetryCount());

        switch (taskType) {
            case FEEDBACK_CLUSTERING -> {
                Long feedbackId = Long.valueOf(targetId);
                feedbackClusteringService.cluster(feedbackId);
                asyncTaskFailureRepository.delete(failure);
                log.info("피드백 클러스터링 재시도 성공: 피드백ID={} 실패작업 기록 삭제 완료", feedbackId);
            }
            case CLUSTER_LABEL_GENERATION -> {
                Long clusterId = Long.valueOf(targetId);
                feedbackClusteringService.createLabel(clusterId);
                asyncTaskFailureRepository.delete(failure);
                log.info("클러스터 라벨 생성 재시도 성공: 클러스터ID={}", clusterId);
            }
            default -> {
                log.warn("알 수 없는 작업 타입: {}", taskType);
                failure.finalFailed();
                throw new IllegalStateException("비동기 작업 종류를 taskType : " + taskType + " 처리할 수 없습니다.");
            }
        }
    }

    private AsyncTaskFailure getById(final Long asyncTaskFailureId) {
        return asyncTaskFailureRepository.findById(asyncTaskFailureId)
                .orElseThrow(() -> new IllegalArgumentException("해당 실패작업 ID(id:" + asyncTaskFailureId + ")로 찾을 수 없습니다."));
    }
}
