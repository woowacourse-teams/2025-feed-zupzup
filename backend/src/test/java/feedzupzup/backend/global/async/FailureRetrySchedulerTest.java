package feedzupzup.backend.global.async;

import static feedzupzup.backend.global.async.FailureStatus.*;
import static feedzupzup.backend.global.async.TargetType.*;
import static feedzupzup.backend.global.async.TaskType.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.feedback.application.FeedbackClusteringService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

class FailureRetrySchedulerTest extends ServiceIntegrationHelper {

    @Autowired
    private FailureRetryScheduler failureRetryScheduler;

    @Autowired
    private AsyncTaskFailureRepository asyncTaskFailureRepository;

    @MockitoSpyBean
    private AsyncTaskFailureService asyncTaskFailureService;

    @MockitoBean
    private FeedbackClusteringService feedbackClusteringService;

    @Nested
    @DisplayName("재시도 스케줄러 테스트")
    class RetrySchedulerTest {

        @Test
        @DisplayName("재시도 가능한 실패 작업들이 스케줄러에 의해 처리된다")
        void when_retryable_failures_exist_then_they_are_processed() {
            // given
            AsyncTaskFailure retryableFailure1 = AsyncTaskFailure.create(
                FEEDBACK_CLUSTERING, FEEDBACK_CLUSTER, "123", "타임아웃 오류 1", true
            );
            AsyncTaskFailure retryableFailure2 = AsyncTaskFailure.create(
                CLUSTER_LABEL_GENERATION, FEEDBACK, "456", "타임아웃 오류 2", true
            );
            AsyncTaskFailure nonRetryableFailure = AsyncTaskFailure.create(
                FEEDBACK_CLUSTERING, FEEDBACK_CLUSTER, "789", "API 키 오류", false
            );
            
            asyncTaskFailureRepository.save(retryableFailure1);
            asyncTaskFailureRepository.save(retryableFailure2);
            asyncTaskFailureRepository.save(nonRetryableFailure);
            
            when(feedbackClusteringService.cluster(123L)).thenReturn(999L);
            doNothing().when(feedbackClusteringService).createLabel(456L);
            
            // when
            failureRetryScheduler.retryFailedTasks();
            
            // then
            verify(asyncTaskFailureService).retry(retryableFailure1.getId());
            verify(asyncTaskFailureService).retry(retryableFailure2.getId());
            
            // 재시도 불가능한 실패는 처리되지 않음
            verify(asyncTaskFailureService, never()).retry(nonRetryableFailure.getId());
        }
    }

    @Nested
    @DisplayName("통합 시나리오 테스트")
    class IntegrationScenarioTest {

        @Test
        @DisplayName("전체 재시도 프로세스가 정상적으로 동작한다")
        void complete_retry_process_works_correctly() {
            // given
            AsyncTaskFailure failure = AsyncTaskFailure.create(
                FEEDBACK_CLUSTERING, FEEDBACK_CLUSTER, "123", "네트워크 타임아웃", true
            );
            asyncTaskFailureRepository.save(failure);
            
            when(feedbackClusteringService.cluster(123L)).thenReturn(999L);
            
            // when
            failureRetryScheduler.retryFailedTasks();
            
            // then
            AsyncTaskFailure updatedFailure = asyncTaskFailureRepository.findById(failure.getId()).orElse(null);
            
            assertAll(
                () -> verify(feedbackClusteringService).cluster(123L),
                () -> assertThat(updatedFailure).isNull(), // 성공 후 삭제됨
                () -> verify(asyncTaskFailureService).retry(failure.getId())
            );
        }

        @Test
        @DisplayName("3회 재시도 후 최종 실패된다")
        void after_three_retries_final_failure_and_alert_sent() {
            // given
            AsyncTaskFailure failure = AsyncTaskFailure.create(
                FEEDBACK_CLUSTERING, FEEDBACK_CLUSTER, "123", "지속적인 네트워크 오류", true
            );
            
            // 이미 2번 재시도한 상태
            failure.incrementRetryCount();
            failure.incrementRetryCount();
            asyncTaskFailureRepository.save(failure);

            // 3번째 재시도도 실패
            when(feedbackClusteringService.cluster(123L))
                    .thenThrow(new RuntimeException("여전히 실패"));

            // when
            failureRetryScheduler.retryFailedTasks();
            
            // then
            AsyncTaskFailure finalFailure = asyncTaskFailureRepository.findById(failure.getId()).orElseThrow();
            
            assertAll(
                () -> assertThat(finalFailure.getRetryCount()).isEqualTo(3),
                () -> assertThat(finalFailure.getStatus()).isEqualTo(FINAL_FAILED)
            );
        }
    }
}
