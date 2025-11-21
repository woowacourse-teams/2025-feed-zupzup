package feedzupzup.backend.global.async;

import static feedzupzup.backend.global.async.FailureStatus.*;
import static feedzupzup.backend.global.async.TargetType.*;
import static feedzupzup.backend.global.async.TaskType.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.feedback.application.FeedbackClusteringService;
import feedzupzup.backend.global.async.exception.NonRetryableException;
import feedzupzup.backend.global.async.exception.RetryableException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class AsyncTaskFailureServiceTest extends ServiceIntegrationHelper {

    @Autowired
    private AsyncTaskFailureService asyncTaskFailureService;

    @Autowired
    private AsyncTaskFailureRepository asyncTaskFailureRepository;

    @MockitoBean
    private FeedbackClusteringService feedbackClusteringService;

    @MockitoBean
    private AsyncFailureAlertService asyncFailureAlertService;

    @Nested
    @DisplayName("실패 기록 테스트")
    class RecordFailureTest {

        @Test
        @DisplayName("RetryableException 발생 시 재시도 가능한 실패로 기록된다")
        void when_retryable_exception_then_record_as_retryable() {
            // given
            RetryableException exception = new RetryableException("VoyageAI 타임아웃 발생");
            
            // when
            Long failureId = asyncTaskFailureService.recordFailure(
                FEEDBACK_CLUSTERING, 
                FEEDBACK_CLUSTER, 
                "123", 
                exception
            );
            
            // then
            AsyncTaskFailure failure = asyncTaskFailureRepository.findById(failureId).orElseThrow();
            assertAll(
                () -> assertThat(failure.getTaskType()).isEqualTo(FEEDBACK_CLUSTERING),
                () -> assertThat(failure.getTargetType()).isEqualTo(FEEDBACK_CLUSTER),
                () -> assertThat(failure.getTargetId()).isEqualTo("123"),
                () -> assertThat(failure.getErrorMessage()).isEqualTo("VoyageAI 타임아웃 발생"),
                () -> assertThat(failure.isRetryable()).isTrue(),
                () -> assertThat(failure.getStatus()).isEqualTo(PENDING),
                () -> assertThat(failure.getRetryCount()).isEqualTo(0)
            );
        }

        @Test
        @DisplayName("NonRetryableException 발생 시 재시도 불가능한 최종 실패로 기록된다")
        void when_non_retryable_exception_then_record_as_final_failed() {
            // given
            NonRetryableException exception = new NonRetryableException("잘못된 API 키");
            
            // when
            Long failureId = asyncTaskFailureService.recordFailure(
                CLUSTER_LABEL_GENERATION, 
                FEEDBACK, 
                "456", 
                exception
            );
            
            // then
            AsyncTaskFailure failure = asyncTaskFailureRepository.findById(failureId).orElseThrow();
            assertAll(
                () -> assertThat(failure.getTaskType()).isEqualTo(CLUSTER_LABEL_GENERATION),
                () -> assertThat(failure.getTargetType()).isEqualTo(FEEDBACK),
                () -> assertThat(failure.getTargetId()).isEqualTo("456"),
                () -> assertThat(failure.getErrorMessage()).isEqualTo("잘못된 API 키"),
                () -> assertThat(failure.isRetryable()).isFalse(),
                () -> assertThat(failure.getStatus()).isEqualTo(FINAL_FAILED),
                () -> assertThat(failure.getRetryCount()).isEqualTo(0)
            );
        }

        @Test
        @DisplayName("일반 Exception 발생 시 재시도 불가능한 최종 실패로 기록된다")
        void when_generic_exception_then_record_as_non_retryable() {
            // given
            RuntimeException exception = new RuntimeException("예상치 못한 오류");
            
            // when
            Long failureId = asyncTaskFailureService.recordFailure(
                FEEDBACK_CLUSTERING, 
                FEEDBACK_CLUSTER, 
                "789", 
                exception
            );
            
            // then
            AsyncTaskFailure failure = asyncTaskFailureRepository.findById(failureId).orElseThrow();
            assertAll(
                () -> assertThat(failure.isRetryable()).isFalse(),
                () -> assertThat(failure.getStatus()).isEqualTo(FINAL_FAILED),
                () -> assertThat(failure.getErrorMessage()).isEqualTo("예상치 못한 오류")
            );
        }
    }

    @Nested
    @DisplayName("최종 실패 알람 테스트")
    class AlertFinalFailTest {

        @Test
        @DisplayName("최종 실패 상태인 경우 알람이 발송된다")
        void when_final_failed_then_alert_is_sent() {
            // given
            AsyncTaskFailure failure = AsyncTaskFailure.create(
                FEEDBACK_CLUSTERING, FEEDBACK_CLUSTER, "123", "API 키 오류", false
            );
            asyncTaskFailureRepository.save(failure);

            // when
            asyncTaskFailureService.alertFinalFail(failure.getId());

            // then
            verify(asyncFailureAlertService).alert(failure.getId());
        }

        @Test
        @DisplayName("최종 실패 상태가 아닌 경우 알람이 발송되지 않는다")
        void when_not_final_failed_then_alert_is_not_sent() {
            // given
            AsyncTaskFailure failure = AsyncTaskFailure.create(
                FEEDBACK_CLUSTERING, FEEDBACK_CLUSTER, "123", "타임아웃 오류", true
            );
            asyncTaskFailureRepository.save(failure);

            // when
            asyncTaskFailureService.alertFinalFail(failure.getId());

            // then
            verifyNoInteractions(asyncFailureAlertService);
        }
    }

    @Nested
    @DisplayName("작업 재시도 테스트")
    class RetryTaskTest {

        @Test
        @DisplayName("피드백 클러스터링 작업이 성공적으로 재시도된다")
        void when_feedback_clustering_retry_then_success() {
            // given
            AsyncTaskFailure failure = AsyncTaskFailure.create(
                FEEDBACK_CLUSTERING, FEEDBACK_CLUSTER, "123", "타임아웃 오류", true
            );
            asyncTaskFailureRepository.save(failure);
            
            when(feedbackClusteringService.cluster(123L)).thenReturn(456L);
            
            // when
            asyncTaskFailureService.retry(failure.getId());
            
            // then
            assertAll(
                () -> verify(feedbackClusteringService).cluster(123L),
                () -> assertThat(asyncTaskFailureRepository.findById(failure.getId())).isEmpty()
            );
        }

        @Test
        @DisplayName("클러스터 라벨 생성 작업이 성공적으로 재시도된다")
        void when_cluster_label_generation_retry_then_success() {
            // given
            AsyncTaskFailure failure = AsyncTaskFailure.create(
                CLUSTER_LABEL_GENERATION, FEEDBACK, "789", "API 오류", true
            );
            asyncTaskFailureRepository.save(failure);
            
            doNothing().when(feedbackClusteringService).createLabel(789L);
            
            // when
            asyncTaskFailureService.retry(failure.getId());
            
            // then
            assertAll(
                () -> verify(feedbackClusteringService).createLabel(789L),
                () -> assertThat(asyncTaskFailureRepository.findById(failure.getId())).isEmpty()
            );
        }

        @Test
        @DisplayName("존재하지 않는 실패 작업 ID로 재시도 시 예외가 발생한다")
        void when_retry_with_non_existing_id_then_throw_exception() {
            // given
            Long nonExistingId = 999L;
            
            // when & then
            assertThatThrownBy(() -> asyncTaskFailureService.retry(nonExistingId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 실패작업 ID(id:" + nonExistingId + ")로 찾을 수 없습니다");
        }
    }
}
