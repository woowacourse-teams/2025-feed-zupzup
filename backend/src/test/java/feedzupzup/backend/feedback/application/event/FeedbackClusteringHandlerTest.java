package feedzupzup.backend.feedback.application.event;

import static feedzupzup.backend.global.async.TargetType.*;
import static feedzupzup.backend.global.async.TaskType.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import feedzupzup.backend.category.domain.Category;
import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.OrganizationCategoryRepository;
import feedzupzup.backend.category.fixture.OrganizationCategoryFixture;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.feedback.application.FeedbackClusteringService;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.domain.event.FeedbackCreatedEvent2;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.global.async.AsyncFailureAlertService;
import feedzupzup.backend.global.async.AsyncTaskFailure;
import feedzupzup.backend.global.async.AsyncTaskFailureRepository;
import feedzupzup.backend.global.async.AsyncTaskFailureService;
import feedzupzup.backend.global.async.exception.NonRetryableException;
import feedzupzup.backend.global.async.exception.RetryableException;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class FeedbackClusteringHandlerTest extends ServiceIntegrationHelper {

    @Autowired
    private FeedbackClusteringHandler feedbackClusteringHandler;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationCategoryRepository organizationCategoryRepository;

    @Autowired
    private AsyncTaskFailureRepository asyncTaskFailureRepository;

    @Autowired
    private AsyncTaskFailureService asyncTaskFailureService;

    @MockitoBean
    private FeedbackClusteringService feedbackClusteringService;

    @MockitoBean
    private AsyncFailureAlertService asyncFailureAlertService;

    @Nested
    @DisplayName("피드백 클러스터링 성공 테스트")
    class ClusteringSuccessTest {

        @Test
        @DisplayName("정상적인 피드백 생성 이벤트 처리 시 클러스터링과 라벨 생성이 모두 성공한다")
        void when_normal_feedback_event_then_clustering_and_label_generation_succeed() {
            // given
            Organization organization = organizationRepository.save(OrganizationFixture.createAllBlackBox());
            OrganizationCategory category = organizationCategoryRepository.save(
                OrganizationCategoryFixture.createOrganizationCategory(organization, Category.SUGGESTION)
            );
            Feedback feedback = feedbackRepository.save(
                FeedbackFixture.createFeedback(organization, "클러스터링 테스트용 피드백", category)
            );
            
            FeedbackCreatedEvent2 event = new FeedbackCreatedEvent2(feedback.getId());
            
            when(feedbackClusteringService.cluster(feedback.getId())).thenReturn(999L);
            doNothing().when(feedbackClusteringService).createLabel(999L);
            
            // when
            feedbackClusteringHandler.handleFeedbackCreatedEvent(event);
            
            // then
            await().atMost(1, SECONDS)
                    .untilAsserted(() ->
                            verify(feedbackClusteringService).cluster(feedback.getId())
                    );
            assertAll(
                () -> verify(feedbackClusteringService).cluster(feedback.getId()),
                () -> verify(feedbackClusteringService).createLabel(999L),
                () -> assertThat(asyncTaskFailureRepository.findAll()).isEmpty()
            );
        }

        @Test
        @DisplayName("클러스터링은 성공하지만 라벨 생성이 실패하는 경우 라벨 생성 실패만 기록된다")
        void when_clustering_succeeds_but_label_generation_fails_then_only_label_failure_recorded() {
            // given
            Organization organization = organizationRepository.save(OrganizationFixture.createAllBlackBox());
            OrganizationCategory category = organizationCategoryRepository.save(
                OrganizationCategoryFixture.createOrganizationCategory(organization, Category.QUESTION)
            );
            Feedback feedback = feedbackRepository.save(
                FeedbackFixture.createFeedback(organization, "클러스터링은 성공, 라벨 실패 케이스", category)
            );
            
            FeedbackCreatedEvent2 event = new FeedbackCreatedEvent2(feedback.getId());
            
            when(feedbackClusteringService.cluster(feedback.getId())).thenReturn(777L);
            doThrow(new RetryableException("OpenAI API 타임아웃"))
                .when(feedbackClusteringService).createLabel(777L);
            
            // when
            feedbackClusteringHandler.handleFeedbackCreatedEvent(event);
            
            // then
            await().atMost(1, SECONDS)
                    .untilAsserted(() ->
                            verify(feedbackClusteringService).cluster(feedback.getId())
                    );
            List<AsyncTaskFailure> failures = asyncTaskFailureRepository.findAll();
            assertAll(
                () -> verify(feedbackClusteringService).cluster(feedback.getId()),
                () -> verify(feedbackClusteringService).createLabel(777L),
                () -> assertThat(failures).hasSize(1),
                () -> {
                    AsyncTaskFailure failure = failures.get(0);
                    assertAll(
                        () -> assertThat(failure.getTaskType()).isEqualTo(CLUSTER_LABEL_GENERATION),
                        () -> assertThat(failure.getTargetType()).isEqualTo(FEEDBACK),
                        () -> assertThat(failure.getTargetId()).isEqualTo("777"),
                        () -> assertThat(failure.getErrorMessage()).isEqualTo("OpenAI API 타임아웃"),
                        () -> assertThat(failure.isRetryable()).isTrue()
                    );
                }
            );
        }
    }

    @Nested
    @DisplayName("피드백 클러스터링 실패 테스트")
    class ClusteringFailureTest {

        @Test
        @DisplayName("재시도 가능한 예외 발생 시 실패가 기록되고 알람이 발송된다")
        void when_retryable_exception_occurs_then_failure_recorded_and_alert_sent() {
            // given
            Organization organization = organizationRepository.save(OrganizationFixture.createAllBlackBox());
            OrganizationCategory category = organizationCategoryRepository.save(
                OrganizationCategoryFixture.createOrganizationCategory(organization, Category.FEEDBACK)
            );
            Feedback feedback = feedbackRepository.save(
                FeedbackFixture.createFeedback(organization, "네트워크 오류 테스트", category)
            );
            
            FeedbackCreatedEvent2 event = new FeedbackCreatedEvent2(feedback.getId());
            
            when(feedbackClusteringService.cluster(feedback.getId()))
                .thenThrow(new RetryableException("VoyageAI 연결 타임아웃"));
            
            // when
            feedbackClusteringHandler.handleFeedbackCreatedEvent(event);
            
            // then
            await().atMost(1, SECONDS)
                    .untilAsserted(() ->
                            verify(feedbackClusteringService).cluster(feedback.getId())
                    );
            List<AsyncTaskFailure> failures = asyncTaskFailureRepository.findAll();
            assertAll(
                () -> verify(feedbackClusteringService).cluster(feedback.getId()),
                () -> verify(feedbackClusteringService, never()).createLabel(any()),
                () -> assertThat(failures).hasSize(1),
                () -> {
                    AsyncTaskFailure failure = failures.get(0);
                    assertAll(
                        () -> assertThat(failure.getTaskType()).isEqualTo(FEEDBACK_CLUSTERING),
                        () -> assertThat(failure.getTargetType()).isEqualTo(FEEDBACK_CLUSTER),
                        () -> assertThat(failure.getTargetId()).isEqualTo(feedback.getId().toString()),
                        () -> assertThat(failure.getErrorMessage()).isEqualTo("VoyageAI 연결 타임아웃"),
                        () -> assertThat(failure.isRetryable()).isTrue()
                    );
                },
                () -> verify(asyncFailureAlertService).alert(failures.get(0).getId())
            );
        }

        @Test
        @DisplayName("재시도 불가능한 예외 발생 시 최종 실패로 기록되고 알람이 발송된다")
        void when_non_retryable_exception_occurs_then_final_failure_recorded_and_alert_sent() {
            // given
            Organization organization = organizationRepository.save(OrganizationFixture.createAllBlackBox());
            OrganizationCategory category = organizationCategoryRepository.save(
                OrganizationCategoryFixture.createOrganizationCategory(organization, Category.COMPLIMENT)
            );
            Feedback feedback = feedbackRepository.save(
                FeedbackFixture.createFeedback(organization, "인증 오류 테스트", category)
            );
            
            FeedbackCreatedEvent2 event = new FeedbackCreatedEvent2(feedback.getId());
            
            when(feedbackClusteringService.cluster(feedback.getId()))
                .thenThrow(new NonRetryableException("잘못된 API 키"));
            
            // when
            feedbackClusteringHandler.handleFeedbackCreatedEvent(event);
            
            // then
            await().atMost(1, SECONDS)
                    .untilAsserted(() ->
                            verify(feedbackClusteringService).cluster(feedback.getId())
                    );
            List<AsyncTaskFailure> failures = asyncTaskFailureRepository.findAll();
            assertAll(
                () -> verify(feedbackClusteringService).cluster(feedback.getId()),
                () -> verify(feedbackClusteringService, never()).createLabel(any()),
                () -> assertThat(failures).hasSize(1),
                () -> {
                    AsyncTaskFailure failure = failures.get(0);
                    assertAll(
                        () -> assertThat(failure.getTaskType()).isEqualTo(FEEDBACK_CLUSTERING),
                        () -> assertThat(failure.getTargetType()).isEqualTo(FEEDBACK_CLUSTER),
                        () -> assertThat(failure.getTargetId()).isEqualTo(feedback.getId().toString()),
                        () -> assertThat(failure.getErrorMessage()).isEqualTo("잘못된 API 키"),
                        () -> assertThat(failure.isRetryable()).isFalse()
                    );
                },
                () -> verify(asyncFailureAlertService).alert(failures.get(0).getId())
            );
        }

        @Test
        @DisplayName("일반적인 런타임 예외 발생 시 재시도 불가능한 실패로 기록된다")
        void when_generic_runtime_exception_occurs_then_non_retryable_failure_recorded() {
            // given
            Organization organization = organizationRepository.save(OrganizationFixture.createAllBlackBox());
            OrganizationCategory category = organizationCategoryRepository.save(
                OrganizationCategoryFixture.createOrganizationCategory(organization, Category.REPORT)
            );
            Feedback feedback = feedbackRepository.save(
                FeedbackFixture.createFeedback(organization, "예상치 못한 오류 테스트", category)
            );
            
            FeedbackCreatedEvent2 event = new FeedbackCreatedEvent2(feedback.getId());
            
            when(feedbackClusteringService.cluster(feedback.getId()))
                .thenThrow(new RuntimeException("예상치 못한 오류가 발생했습니다"));
            
            // when
            feedbackClusteringHandler.handleFeedbackCreatedEvent(event);
            
            // then
            await().atMost(1, SECONDS)
                    .untilAsserted(() ->
                            verify(feedbackClusteringService).cluster(feedback.getId())
                    );
            List<AsyncTaskFailure> failures = asyncTaskFailureRepository.findAll();
            assertAll(
                () -> verify(feedbackClusteringService).cluster(feedback.getId()),
                () -> verify(feedbackClusteringService, never()).createLabel(any()),
                () -> assertThat(failures).hasSize(1),
                () -> {
                    AsyncTaskFailure failure = failures.get(0);
                    assertAll(
                        () -> assertThat(failure.getTaskType()).isEqualTo(FEEDBACK_CLUSTERING),
                        () -> assertThat(failure.getTargetId()).isEqualTo(feedback.getId().toString()),
                        () -> assertThat(failure.getErrorMessage()).isEqualTo("예상치 못한 오류가 발생했습니다"),
                        () -> assertThat(failure.isRetryable()).isFalse()
                    );
                }
            );
        }
    }

    @Nested
    @DisplayName("라벨 생성 실패 테스트")
    class LabelGenerationFailureTest {

        @Test
        @DisplayName("라벨 생성에서 재시도 가능한 예외 발생 시 실패가 기록된다")
        void when_label_generation_retryable_exception_then_failure_recorded() {
            // given
            Organization organization = organizationRepository.save(OrganizationFixture.createAllBlackBox());
            OrganizationCategory category = organizationCategoryRepository.save(
                OrganizationCategoryFixture.createOrganizationCategory(organization, Category.SHARING)
            );
            Feedback feedback = feedbackRepository.save(
                FeedbackFixture.createFeedback(organization, "라벨 생성 실패 테스트", category)
            );
            
            FeedbackCreatedEvent2 event = new FeedbackCreatedEvent2(feedback.getId());
            
            when(feedbackClusteringService.cluster(feedback.getId())).thenReturn(555L);
            doThrow(new RetryableException("OpenAI Rate limit 초과"))
                .when(feedbackClusteringService).createLabel(555L);
            
            // when
            feedbackClusteringHandler.handleFeedbackCreatedEvent(event);
            
            // then
            await().atMost(1, SECONDS)
                    .untilAsserted(() ->
                            verify(feedbackClusteringService).cluster(feedback.getId())
                    );
            List<AsyncTaskFailure> failures = asyncTaskFailureRepository.findAll();
            assertAll(
                () -> verify(feedbackClusteringService).cluster(feedback.getId()),
                () -> verify(feedbackClusteringService).createLabel(555L),
                () -> assertThat(failures).hasSize(1),
                () -> {
                    AsyncTaskFailure failure = failures.get(0);
                    assertAll(
                        () -> assertThat(failure.getTaskType()).isEqualTo(CLUSTER_LABEL_GENERATION),
                        () -> assertThat(failure.getTargetType()).isEqualTo(FEEDBACK),
                        () -> assertThat(failure.getTargetId()).isEqualTo("555"),
                        () -> assertThat(failure.getErrorMessage()).isEqualTo("OpenAI Rate limit 초과"),
                        () -> assertThat(failure.isRetryable()).isTrue()
                    );
                }
            );
        }

        @Test
        @DisplayName("라벨 생성에서 재시도 불가능한 예외 발생 시 최종 실패로 기록된다")
        void when_label_generation_non_retryable_exception_then_final_failure_recorded() {
            // given
            Organization organization = organizationRepository.save(OrganizationFixture.createAllBlackBox());
            OrganizationCategory category = organizationCategoryRepository.save(
                OrganizationCategoryFixture.createOrganizationCategory(organization, Category.ETC)
            );
            Feedback feedback = feedbackRepository.save(
                FeedbackFixture.createFeedback(organization, "라벨 생성 최종 실패 테스트", category)
            );
            
            FeedbackCreatedEvent2 event = new FeedbackCreatedEvent2(feedback.getId());
            
            when(feedbackClusteringService.cluster(feedback.getId())).thenReturn(888L);
            doThrow(new NonRetryableException("OpenAI 모델 접근 권한 없음"))
                .when(feedbackClusteringService).createLabel(888L);
            
            // when
            feedbackClusteringHandler.handleFeedbackCreatedEvent(event);
            
            // then
            await().atMost(1, SECONDS)
                    .untilAsserted(() ->
                            verify(feedbackClusteringService).cluster(feedback.getId())
                    );
            List<AsyncTaskFailure> failures = asyncTaskFailureRepository.findAll();
            assertAll(
                () -> verify(feedbackClusteringService).cluster(feedback.getId()),
                () -> verify(feedbackClusteringService).createLabel(888L),
                () -> assertThat(failures).hasSize(1),
                () -> {
                    AsyncTaskFailure failure = failures.get(0);
                    assertAll(
                        () -> assertThat(failure.getTaskType()).isEqualTo(CLUSTER_LABEL_GENERATION),
                        () -> assertThat(failure.getTargetType()).isEqualTo(FEEDBACK),
                        () -> assertThat(failure.getTargetId()).isEqualTo("888"),
                        () -> assertThat(failure.getErrorMessage()).isEqualTo("OpenAI 모델 접근 권한 없음"),
                        () -> assertThat(failure.isRetryable()).isFalse()
                    );
                }
            );
        }
    }

    @Nested
    @DisplayName("실패 처리 검증 테스트")
    class FailureHandlingVerificationTest {

        @Test
        @DisplayName("실패 기록 후 적절한 서비스 메서드가 호출되는지 검증")
        void verify_failure_service_methods_are_called_correctly() {
            // given
            Organization organization = organizationRepository.save(OrganizationFixture.createAllBlackBox());
            OrganizationCategory category = organizationCategoryRepository.save(
                OrganizationCategoryFixture.createOrganizationCategory(organization)
            );
            Feedback feedback = feedbackRepository.save(
                FeedbackFixture.createFeedback(organization, "서비스 메서드 호출 검증", category)
            );
            
            FeedbackCreatedEvent2 event = new FeedbackCreatedEvent2(feedback.getId());
            RetryableException exception = new RetryableException("네트워크 연결 실패");
            
            when(feedbackClusteringService.cluster(feedback.getId())).thenThrow(exception);
            
            // when
            feedbackClusteringHandler.handleFeedbackCreatedEvent(event);
            
            // then
            await().atMost(1, SECONDS)
                    .untilAsserted(() ->
                            verify(feedbackClusteringService).cluster(feedback.getId())
                    );
            verify(asyncTaskFailureService).recordFailure(
                FEEDBACK_CLUSTERING, 
                FEEDBACK_CLUSTER, 
                feedback.getId().toString(), 
                exception
            );
            verify(asyncTaskFailureService).alertFinalFail(anyLong());
        }
    }
}
