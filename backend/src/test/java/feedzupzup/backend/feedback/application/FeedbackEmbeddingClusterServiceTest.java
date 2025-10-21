package feedzupzup.backend.feedback.application;

import static feedzupzup.backend.category.domain.Category.SUGGESTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.OrganizationCategoryRepository;
import feedzupzup.backend.category.fixture.OrganizationCategoryFixture;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.feedback.domain.ClusterLabelGenerator;
import feedzupzup.backend.feedback.domain.EmbeddingCluster;
import feedzupzup.backend.feedback.domain.EmbeddingClusterRepository;
import feedzupzup.backend.feedback.domain.EmbeddingExtractor;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackEmbeddingCluster;
import feedzupzup.backend.feedback.domain.FeedbackEmbeddingClusterRepository;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

class FeedbackEmbeddingClusterServiceTest extends ServiceIntegrationHelper {

    @Autowired
    private FeedbackClusteringService feedbackClusteringService;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationCategoryRepository organizationCategoryRepository;

    @Autowired
    private EmbeddingClusterRepository embeddingClusterRepository;

    @Autowired
    private FeedbackEmbeddingClusterRepository feedbackEmbeddingClusterRepository;

    @MockitoBean
    private EmbeddingExtractor embeddingExtractor;

    @MockitoBean
    private ClusterLabelGenerator clusterLabelGenerator;

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
    @Transactional
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    class ClusterWithMockTest {

        @Test
        @DisplayName("새로운 피드백이 유사한 기존 클러스터에 할당된다")
        void cluster_AssignToExistingCluster_WhenSimilarityIsHigh() {
            // given
            // 기존 피드백 생성 및 클러스터링
            final Feedback existingFeedback = FeedbackFixture.createFeedbackWithContent(
                    organization, "기존 피드백", organizationCategory);
            final Feedback savedExistingFeedback = feedbackRepository.save(existingFeedback);
            
            // 새로운 피드백 (클러스터링 되지 않은 상태)
            final Feedback newFeedback = FeedbackFixture.createFeedbackWithContent(
                    organization, "새로운 피드백", organizationCategory);
            final Feedback savedNewFeedback = feedbackRepository.save(newFeedback);

            // 기존 피드백 임베딩 설정 (첫 번째 호출)
            final double[] existingEmbedding = {0.1, 0.2, 0.3, 0.4, 0.5};
            // 새로운 임베딩은 기존 임베딩과 유사 (0.9 유사도) (두 번째 호출)
            final double[] newEmbedding = {0.11, 0.21, 0.31, 0.41, 0.51};
            given(embeddingExtractor.extract("기존 피드백")).willReturn(existingEmbedding);
            given(embeddingExtractor.extract("새로운 피드백")).willReturn(newEmbedding);
            
            // 기존 피드백 클러스터링 수행
            FeedbackEmbeddingCluster existingCluster = feedbackClusteringService.cluster(savedExistingFeedback.getId());
            final Long existingClusterId = existingCluster.getEmbeddingCluster().getId();

            // when
            FeedbackEmbeddingCluster result = feedbackClusteringService.cluster(savedNewFeedback.getId());

            // then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getEmbeddingCluster().getId()).isEqualTo(existingClusterId),
                    () -> assertThat(result.getSimilarityScore()).isGreaterThan(0.75)
            );
            verify(embeddingExtractor).extract("기존 피드백");
            verify(embeddingExtractor).extract("새로운 피드백");
        }

        @Test
        @DisplayName("여러 클러스터 중 가장 유사도가 높은 클러스터에 할당된다")
        void cluster_AssignToMostSimilarCluster() {
            // given
            // 첫 번째 피드백 생성 및 클러스터링
            final double[] embedding1 = {0.1, 0.2, 0.3, 0.4, 0.5};
            final Feedback feedback1 = FeedbackFixture.createFeedbackWithContent(
                    organization, "첫 번째 클러스터", organizationCategory);
            final Feedback savedFeedback1 = feedbackRepository.save(feedback1);
            given(embeddingExtractor.extract("첫 번째 클러스터")).willReturn(embedding1);
            FeedbackEmbeddingCluster cluster1 = feedbackClusteringService.cluster(savedFeedback1.getId());
            final Long clusterId1 = cluster1.getEmbeddingCluster().getId();
            
            // 두 번째 피드백 생성 및 클러스터링 (다른 클러스터)
            final double[] embedding2 = {0.11, 0.21, 0.31, 0.41, 0.51};
            final Feedback feedback2 = FeedbackFixture.createFeedbackWithContent(
                    organization, "두 번째 클러스터", organizationCategory);
            final Feedback savedFeedback2 = feedbackRepository.save(feedback2);
            given(embeddingExtractor.extract("두 번째 클러스터")).willReturn(embedding2);
            FeedbackEmbeddingCluster cluster2 = feedbackClusteringService.cluster(savedFeedback2.getId());
            final Long clusterId2 = cluster2.getEmbeddingCluster().getId();

            // 새로운 피드백 생성
            final Feedback newFeedback = FeedbackFixture.createFeedbackWithContent(
                    organization, "새로운 피드백", organizationCategory);
            final Feedback savedNewFeedback = feedbackRepository.save(newFeedback);
            // 두 번째 클러스터와 더 유사한 임베딩 (첫 번째 클러스터: 유사도 ~0.87, 두 번째 클러스터: 유사도 ~0.95)
            final double[] newEmbedding = {0.12, 0.22, 0.32, 0.42, 0.52};
            given(embeddingExtractor.extract("새로운 피드백")).willReturn(newEmbedding);

            // when
            FeedbackEmbeddingCluster result = feedbackClusteringService.cluster(savedNewFeedback.getId());

            // then - 더 유사한 두 번째 클러스터에 할당되어야 함
            assertThat(result.getEmbeddingCluster().getId()).isEqualTo(clusterId2);
            verify(embeddingExtractor).extract("첫 번째 클러스터");
            verify(embeddingExtractor).extract("두 번째 클러스터");
            verify(embeddingExtractor).extract("새로운 피드백");
        }

        @Test
        @DisplayName("새로운 피드백이 기존 클러스터와 유사도가 낮으면 새 클러스터를 생성한다")
        void cluster_CreateNewCluster_WhenSimilarityIsLow() {
            // given
            // 기존 피드백 생성 및 클러스터링
            final Feedback existingFeedback = FeedbackFixture.createFeedbackWithContent(
                    organization, "기존 피드백", organizationCategory);
            final Feedback savedExistingFeedback = feedbackRepository.save(existingFeedback);
            
            // 기존 피드백 임베딩 설정
            final double[] existingEmbedding = {0.1, 0.2, 0.3, 0.4, 0.5};
            given(embeddingExtractor.extract("기존 피드백")).willReturn(existingEmbedding);
            
            // 기존 피드백 클러스터링 수행
            FeedbackEmbeddingCluster existingCluster = feedbackClusteringService.cluster(savedExistingFeedback.getId());
            final Long existingClusterId = existingCluster.getEmbeddingCluster().getId();

            // 새로운 피드백 생성  
            final Feedback newFeedback = FeedbackFixture.createFeedbackWithContent(
                    organization, "새로운 피드백", organizationCategory);
            final Feedback savedNewFeedback = feedbackRepository.save(newFeedback);
            // 새로운 임베딩은 기존 임베딩과 유사도가 낮음 (0.1 유사도)
            final double[] newEmbedding = {-0.3, -0.2, 0.1, 0.2, 0.3};
            given(embeddingExtractor.extract("새로운 피드백")).willReturn(newEmbedding);

            // when
            FeedbackEmbeddingCluster result = feedbackClusteringService.cluster(savedNewFeedback.getId());

            // then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getEmbeddingCluster().getId()).isNotEqualTo(existingClusterId),
                    () -> assertThat(result.getSimilarityScore()).isEqualTo(1.0) // 새 클러스터는 유사도 1.0
            );
        }

        @Test
        @DisplayName("다른 조직의 클러스터는 고려하지 않는다")
        void cluster_IgnoresOtherOrganizationClusters() {
            // given
            final Organization otherOrganization = organizationRepository.save(OrganizationFixture.createAllBlackBox());
            final OrganizationCategory otherOrgCategory = organizationCategoryRepository.save(
                    OrganizationCategoryFixture.createOrganizationCategory(otherOrganization, SUGGESTION));

            // 다른 조직의 피드백 생성 및 클러스터링
            final double[] otherOrgEmbedding = {0.1, 0.2, 0.3, 0.4, 0.5};
            final Feedback otherOrgFeedback = FeedbackFixture.createFeedbackWithContent(
                    otherOrganization, "다른 조직 피드백", otherOrgCategory);
            final Feedback savedOtherOrgFeedback = feedbackRepository.save(otherOrgFeedback);
            given(embeddingExtractor.extract("다른 조직 피드백")).willReturn(otherOrgEmbedding);
            FeedbackEmbeddingCluster otherOrgCluster = feedbackClusteringService.cluster(savedOtherOrgFeedback.getId());
            final Long otherOrgClusterId = otherOrgCluster.getEmbeddingCluster().getId();

            // 내 조직의 새 피드백
            final Feedback myFeedback = FeedbackFixture.createFeedbackWithContent(
                    organization, "내 조직 피드백", organizationCategory);
            final Feedback savedMyFeedback = feedbackRepository.save(myFeedback);

            // 다른 조직과 매우 유사한 임베딩 (하지만 무시되어야 함)
            final double[] myEmbedding = {0.11, 0.21, 0.31, 0.41, 0.51};
            given(embeddingExtractor.extract("내 조직 피드백")).willReturn(myEmbedding);

            // when
            FeedbackEmbeddingCluster result = feedbackClusteringService.cluster(savedMyFeedback.getId());

            // then
            assertAll(
                    () -> assertThat(result).isNotNull(),
                    () -> assertThat(result.getEmbeddingCluster().getId()).isNotEqualTo(otherOrgClusterId),
                    () -> assertThat(result.getSimilarityScore()).isEqualTo(1.0) // 새 클러스터 생성
            );
        }
    }

    @Nested
    @DisplayName("클러스터 라벨 생성 테스트")
    @Transactional
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    class CreateLabelTest {

        @Test
        @DisplayName("클러스터 크기가 임계치에 도달하면 라벨을 생성한다")
        void createLabel_GenerateLabel_WhenClusterSizeMatchesThreshold() {
            // given
            final Feedback feedback = FeedbackFixture.createFeedbackWithContent(
                    organization, "테스트 피드백", organizationCategory);
            final Feedback savedFeedback = feedbackRepository.save(feedback);
            final double[] embedding = {0.1, 0.2, 0.3, 0.4, 0.5};
            given(embeddingExtractor.extract("테스트 피드백")).willReturn(embedding);
            given(clusterLabelGenerator.generate(anyList())).willReturn("생성된 라벨");

            FeedbackEmbeddingCluster cluster = feedbackClusteringService.cluster(savedFeedback.getId());

            // when
            feedbackClusteringService.createLabel(cluster);

            // then
            verify(clusterLabelGenerator).generate(List.of("테스트 피드백"));
            assertThat(cluster.getEmbeddingCluster().getLabel()).isEqualTo("생성된 라벨");
        }

        @Test
        @DisplayName("클러스터 크기가 5개에 도달하면 라벨을 생성한다")
        void createLabel_GenerateLabel_WhenClusterSizeReachesFive() {
            // given
            final double[] embedding = {0.1, 0.2, 0.3, 0.4, 0.5};
            given(embeddingExtractor.extract(any(String.class))).willReturn(embedding);
            given(clusterLabelGenerator.generate(anyList())).willReturn("5개 피드백 라벨");

            FeedbackEmbeddingCluster firstCluster = null;
            for (int i = 1; i <= 5; i++) {
                final Feedback feedback = FeedbackFixture.createFeedbackWithContent(
                        organization, "피드백 " + i, organizationCategory);
                final Feedback savedFeedback = feedbackRepository.save(feedback);
                FeedbackEmbeddingCluster cluster = feedbackClusteringService.cluster(savedFeedback.getId());
                if (i == 1) {
                    firstCluster = cluster;
                }
            }

            // when
            feedbackClusteringService.createLabel(firstCluster);

            // then
            verify(clusterLabelGenerator).generate(anyList());
            assertThat(firstCluster.getEmbeddingCluster().getLabel()).isEqualTo("5개 피드백 라벨");
        }

        @Test
        @DisplayName("클러스터 크기가 임계치가 아니고 라벨이 이미 있으면 라벨을 생성하지 않는다")
        void createLabel_DoNotGenerateLabel_WhenClusterSizeIsNotThresholdAndLabelExists() {
            // given
            final Feedback feedback1 = FeedbackFixture.createFeedbackWithContent(
                    organization, "피드백 1", organizationCategory);
            final Feedback savedFeedback1 = feedbackRepository.save(feedback1);
            final double[] embedding = {0.1, 0.2, 0.3, 0.4, 0.5};
            given(embeddingExtractor.extract(any(String.class))).willReturn(embedding);

            FeedbackEmbeddingCluster firstCluster = feedbackClusteringService.cluster(savedFeedback1.getId());
            firstCluster.getEmbeddingCluster().updateLabel("기존 라벨");

            final Feedback feedback2 = FeedbackFixture.createFeedbackWithContent(
                    organization, "피드백 2", organizationCategory);
            final Feedback savedFeedback2 = feedbackRepository.save(feedback2);
            feedbackClusteringService.cluster(savedFeedback2.getId());

            // when
            feedbackClusteringService.createLabel(firstCluster);

            // then
            verify(clusterLabelGenerator, never()).generate(anyList());
            assertThat(firstCluster.getEmbeddingCluster().getLabel()).isEqualTo("기존 라벨");
        }
    }
}
