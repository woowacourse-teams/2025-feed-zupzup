package feedzupzup.backend.feedback.application;

import static feedzupzup.backend.category.domain.Category.SUGGESTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.OrganizationCategoryRepository;
import feedzupzup.backend.category.fixture.OrganizationCategoryFixture;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.feedback.domain.EmbeddingExtractor;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class FeedbackClusteringServiceTest extends ServiceIntegrationHelper {

    @Autowired
    private FeedbackClusteringService feedbackClusteringService;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationCategoryRepository organizationCategoryRepository;

    @MockitoBean
    private EmbeddingExtractor embeddingExtractor;

    private Organization organization;
    private OrganizationCategory organizationCategory;

    @BeforeEach
    void setUp() {
        organization = organizationRepository.save(OrganizationFixture.createAllBlackBox());
        organizationCategory = organizationCategoryRepository.save(
                OrganizationCategoryFixture.createOrganizationCategory(organization, SUGGESTION));
    }

    @Nested
    @DisplayName("피드백 클러스터링 테스트 - Mock")
    class ClusterWithMockTest {

        @Test
        @DisplayName("새로운 피드백이 유사한 기존 클러스터에 할당된다")
        void cluster_AssignToExistingCluster_WhenSimilarityIsHigh() {
            // given
            final UUID existingClusterId = UUID.fromString("11111111-1111-1111-1111-111111111111");
            
            // 기존 클러스터의 대표 피드백
            final double[] existingEmbedding = {0.1, 0.2, 0.3, 0.4, 0.5};
            final Feedback existingFeedback = FeedbackFixture.createFeedbackWithCluster(
                    organization, "기존 피드백", organizationCategory, existingClusterId, existingEmbedding);
            feedbackRepository.save(existingFeedback);

            // 새로운 피드백 (클러스터링 되지 않은 상태)
            final Feedback newFeedback = FeedbackFixture.createFeedbackWithOrganization(
                    organization, organizationCategory);
            final Feedback savedNewFeedback = feedbackRepository.save(newFeedback);

            // 새로운 임베딩은 기존 임베딩과 유사 (0.9 유사도)
            final double[] newEmbedding = {0.11, 0.21, 0.31, 0.41, 0.51};
            given(embeddingExtractor.extract(any(String.class))).willReturn(newEmbedding);

            // when
            feedbackClusteringService.cluster(savedNewFeedback.getId());

            // then
            final Feedback clusteredFeedback = feedbackRepository.findById(savedNewFeedback.getId()).orElseThrow();
            assertAll(
                    () -> assertThat(clusteredFeedback.getClustering()).isNotNull(),
                    () -> assertThat(clusteredFeedback.getClustering().clusterId()).isEqualTo(existingClusterId),
                    () -> assertThat(clusteredFeedback.getClustering().similarityScore()).isGreaterThan(0.85)
            );
            verify(embeddingExtractor).extract("장소별 피드백");
        }

        @Test
        @DisplayName("새로운 피드백이 기존 클러스터와 유사도가 낮으면 새 클러스터를 생성한다")
        void cluster_CreateNewCluster_WhenSimilarityIsLow() {
            // given
            final UUID existingClusterId = UUID.fromString("11111111-1111-1111-1111-111111111111");
            
            // 기존 클러스터의 대표 피드백
            final double[] existingEmbedding = {0.1, 0.2, 0.3, 0.4, 0.5};
            final Feedback existingFeedback = FeedbackFixture.createFeedbackWithCluster(
                    organization, "기존 피드백", organizationCategory, existingClusterId, existingEmbedding);
            feedbackRepository.save(existingFeedback);

            // 새로운 피드백
            final Feedback newFeedback = FeedbackFixture.createFeedbackWithOrganization(
                    organization, organizationCategory);
            final Feedback savedNewFeedback = feedbackRepository.save(newFeedback);

            // 새로운 임베딩은 기존 임베딩과 유사도가 낮음 (0.1 유사도)
            final double[] newEmbedding = {-0.3, -0.2, 0.1, 0.2, 0.3};
            given(embeddingExtractor.extract(any(String.class))).willReturn(newEmbedding);

            // when
            feedbackClusteringService.cluster(savedNewFeedback.getId());

            // then
            final Feedback clusteredFeedback = feedbackRepository.findById(savedNewFeedback.getId()).orElseThrow();
            assertAll(
                    () -> assertThat(clusteredFeedback.getClustering()).isNotNull(),
                    () -> assertThat(clusteredFeedback.getClustering().clusterId()).isNotEqualTo(existingClusterId),
                    () -> assertThat(clusteredFeedback.getClustering().similarityScore()).isEqualTo(0.0) // 새 클러스터는 유사도 0
            );
        }

        @Test
        @DisplayName("여러 클러스터 중 가장 유사도가 높은 클러스터에 할당된다")
        void cluster_AssignToMostSimilarCluster() {
            // given
            final UUID clusterId1 = UUID.fromString("11111111-1111-1111-1111-111111111111");
            final UUID clusterId2 = UUID.fromString("22222222-2222-2222-2222-222222222222");
            
            // 첫 번째 클러스터 (유사도 0.87)
            final double[] embedding1 = {0.1, 0.2, 0.3, 0.4, 0.5};
            final Feedback feedback1 = FeedbackFixture.createFeedbackWithCluster(
                    organization, "첫 번째 클러스터", organizationCategory, clusterId1, embedding1);
            
            // 두 번째 클러스터 (유사도 0.95 - 더 높음)
            final double[] embedding2 = {0.11, 0.21, 0.31, 0.41, 0.51};
            final Feedback feedback2 = FeedbackFixture.createFeedbackWithCluster(
                    organization, "두 번째 클러스터", organizationCategory, clusterId2, embedding2);

            feedbackRepository.save(feedback1);
            feedbackRepository.save(feedback2);

            // 새로운 피드백
            final Feedback newFeedback = FeedbackFixture.createFeedbackWithOrganization(
                    organization, organizationCategory);
            final Feedback savedNewFeedback = feedbackRepository.save(newFeedback);

            // 두 번째 클러스터와 더 유사한 임베딩
            final double[] newEmbedding = {0.12, 0.22, 0.32, 0.42, 0.52};
            given(embeddingExtractor.extract(any(String.class))).willReturn(newEmbedding);

            // when
            feedbackClusteringService.cluster(savedNewFeedback.getId());

            // then
            final Feedback clusteredFeedback = feedbackRepository.findById(savedNewFeedback.getId()).orElseThrow();
            assertThat(clusteredFeedback.getClustering().clusterId()).isEqualTo(clusterId2);
        }

        @Test
        @DisplayName("다른 조직의 클러스터는 고려하지 않는다")
        void cluster_IgnoresOtherOrganizationClusters() {
            // given
            final Organization otherOrganization = organizationRepository.save(OrganizationFixture.createAllBlackBox());
            final OrganizationCategory otherOrgCategory = organizationCategoryRepository.save(
                    OrganizationCategoryFixture.createOrganizationCategory(otherOrganization, SUGGESTION));

            // 다른 조직의 클러스터
            final UUID otherOrgClusterId = UUID.fromString("99999999-9999-9999-9999-999999999999");
            final double[] otherOrgEmbedding = {0.1, 0.2, 0.3, 0.4, 0.5};
            final Feedback otherOrgFeedback = FeedbackFixture.createFeedbackWithCluster(
                    otherOrganization, "다른 조직 피드백", otherOrgCategory, otherOrgClusterId, otherOrgEmbedding);
            feedbackRepository.save(otherOrgFeedback);

            // 내 조직의 새 피드백
            final Feedback myFeedback = FeedbackFixture.createFeedbackWithOrganization(
                    organization, organizationCategory);
            final Feedback savedMyFeedback = feedbackRepository.save(myFeedback);

            // 다른 조직과 매우 유사한 임베딩 (하지만 무시되어야 함)
            final double[] myEmbedding = {0.11, 0.21, 0.31, 0.41, 0.51};
            given(embeddingExtractor.extract(any(String.class))).willReturn(myEmbedding);

            // when
            feedbackClusteringService.cluster(savedMyFeedback.getId());

            // then
            final Feedback clusteredFeedback = feedbackRepository.findById(savedMyFeedback.getId()).orElseThrow();
            assertAll(
                    () -> assertThat(clusteredFeedback.getClustering()).isNotNull(),
                    () -> assertThat(clusteredFeedback.getClustering().clusterId()).isNotEqualTo(otherOrgClusterId),
                    () -> assertThat(clusteredFeedback.getClustering().similarityScore()).isEqualTo(0.0) // 새 클러스터 생성
            );
        }
    }
}
