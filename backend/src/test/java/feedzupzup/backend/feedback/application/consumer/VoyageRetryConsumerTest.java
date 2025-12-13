package feedzupzup.backend.feedback.application.consumer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.OrganizationCategoryRepository;
import feedzupzup.backend.category.fixture.OrganizationCategoryFixture;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.feedback.application.FeedbackClusteringService;
import feedzupzup.backend.feedback.application.VoyageRetryQueueService;
import feedzupzup.backend.feedback.application.dto.VoyageRetryTask;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class VoyageRetryConsumerTest extends ServiceIntegrationHelper {

    @Autowired
    private VoyageRetryConsumer voyageRetryConsumer;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationCategoryRepository organizationCategoryRepository;

    @MockitoBean
    private FeedbackClusteringService clusteringService;

    @MockitoBean
    private VoyageRetryQueueService voyageRetryQueueService;

    @Test
    @DisplayName("재시도 작업 성공 시 클러스터링 수행 및 Outbox 삭제")
    void consumeRetryTask_success_clustersAndDeletesOutbox() {
        // given
        Organization organization = organizationRepository.save(OrganizationFixture.createAllBlackBox());
        OrganizationCategory category = organizationCategoryRepository.save(
                OrganizationCategoryFixture.createOrganizationCategory(organization)
        );
        Feedback feedback = feedbackRepository.save(
                FeedbackFixture.createFeedback(organization, "재시도 테스트용 피드백", category)
        );

        VoyageRetryTask task = VoyageRetryTask.of(feedback.getId());
        when(clusteringService.clusterForRetry(feedback.getId())).thenReturn(1L);

        // when
        voyageRetryConsumer.consumeRetryTask(task);

        // then
        verify(clusteringService).clusterForRetry(feedback.getId());
        verify(voyageRetryQueueService).deleteOutboxByFeedbackId(feedback.getId());
    }

    @Test
    @DisplayName("클러스터링 실패 시 예외 발생 및 Outbox 유지")
    void consumeRetryTask_clusteringFailure_throwsException() {
        // given
        Long feedbackId = 999L;
        VoyageRetryTask task = VoyageRetryTask.of(feedbackId);

        doThrow(new RuntimeException("클러스터링 실패"))
                .when(clusteringService)
                .clusterForRetry(feedbackId);

        // when & then
        assertThrows(
                RuntimeException.class,
                () -> voyageRetryConsumer.consumeRetryTask(task)
        );

        // Outbox 삭제가 호출되지 않아야 함
        verify(voyageRetryQueueService, never())
                .deleteOutboxByFeedbackId(any());
    }

    @Test
    @DisplayName("멱등성 체크: 이미 클러스터링 완료된 경우 스킵 후 Outbox 삭제")
    void consumeRetryTask_alreadyClustered_skipsAndDeletesOutbox() {
        // given
        Organization organization = organizationRepository.save(OrganizationFixture.createAllBlackBox());
        OrganizationCategory category = organizationCategoryRepository.save(
                OrganizationCategoryFixture.createOrganizationCategory(organization)
        );
        Feedback feedback = feedbackRepository.save(
                FeedbackFixture.createFeedback(organization, "멱등성 테스트용 피드백", category)
        );

        // 이미 클러스터링 완료 상태로 설정
        when(clusteringService.clusterForRetry(feedback.getId())).thenReturn(1L);

        VoyageRetryTask task = VoyageRetryTask.of(feedback.getId());

        // when
        voyageRetryConsumer.consumeRetryTask(task);

        // then
        verify(clusteringService).clusterForRetry(feedback.getId());
        verify(voyageRetryQueueService).deleteOutboxByFeedbackId(feedback.getId());
    }
}
