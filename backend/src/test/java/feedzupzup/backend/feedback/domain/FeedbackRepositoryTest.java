package feedzupzup.backend.feedback.domain;

import static feedzupzup.backend.category.domain.Category.SUGGESTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.OrganizationCategoryRepository;
import feedzupzup.backend.category.fixture.OrganizationCategoryFixture;
import feedzupzup.backend.config.RepositoryHelper;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class FeedbackRepositoryTest extends RepositoryHelper {

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

    private Organization organization;
    private OrganizationCategory organizationCategory;

    @BeforeEach
    void setUp() {
        organization = organizationRepository.save(OrganizationFixture.createAllBlackBox());
        organizationCategory = organizationCategoryRepository.save(
                OrganizationCategoryFixture.createOrganizationCategory(organization, SUGGESTION));
    }

    @Nested
    @DisplayName("상위 클러스터 조회 테스트")
    class FindTopClustersTest {

        @Test
        @DisplayName("피드백 수가 많은 순서대로 클러스터를 조회한다")
        void findTopClusters_ReturnsClustersSortedByFeedbackCount() {
            // given
            // 첫 번째 클러스터 생성 (피드백 3개)
            final EmbeddingCluster cluster1 = embeddingClusterRepository.save(EmbeddingCluster.createEmpty());
            cluster1.updateLabel("첫 번째 클러스터");
            embeddingClusterRepository.save(cluster1);

            // 두 번째 클러스터 생성 (피드백 2개)
            final EmbeddingCluster cluster2 = embeddingClusterRepository.save(EmbeddingCluster.createEmpty());
            cluster2.updateLabel("두 번째 클러스터");
            embeddingClusterRepository.save(cluster2);

            // 세 번째 클러스터 생성 (피드백 1개)
            final EmbeddingCluster cluster3 = embeddingClusterRepository.save(EmbeddingCluster.createEmpty());
            cluster3.updateLabel("세 번째 클러스터");
            embeddingClusterRepository.save(cluster3);

            // 첫 번째 클러스터에 피드백 3개 추가
            createFeedbackWithCluster(organization, "첫 번째 클러스터 피드백1", organizationCategory, cluster1);
            createFeedbackWithCluster(organization, "첫 번째 클러스터 피드백2", organizationCategory, cluster1);
            createFeedbackWithCluster(organization, "첫 번째 클러스터 피드백3", organizationCategory, cluster1);

            // 두 번째 클러스터에 피드백 2개 추가
            createFeedbackWithCluster(organization, "두 번째 클러스터 피드백1", organizationCategory, cluster2);
            createFeedbackWithCluster(organization, "두 번째 클러스터 피드백2", organizationCategory, cluster2);

            // 세 번째 클러스터에 피드백 1개 추가
            createFeedbackWithCluster(organization, "세 번째 클러스터 피드백1", organizationCategory, cluster3);

            // when
            final List<ClusterInfo> result = feedbackRepository.findTopClusters(organization.getUuid(), 10);

            // then
            assertAll(
                    () -> assertThat(result).hasSize(3),
                    () -> assertThat(result.get(0).totalCount()).isEqualTo(3), // 첫 번째 클러스터
                    () -> assertThat(result.get(0).embeddingClusterId()).isEqualTo(cluster1.getId().intValue()),
                    () -> assertThat(result.get(0).label()).isEqualTo("첫 번째 클러스터"),
                    () -> assertThat(result.get(1).totalCount()).isEqualTo(2), // 두 번째 클러스터
                    () -> assertThat(result.get(1).embeddingClusterId()).isEqualTo(cluster2.getId().intValue()),
                    () -> assertThat(result.get(1).label()).isEqualTo("두 번째 클러스터"),
                    () -> assertThat(result.get(2).totalCount()).isEqualTo(1), // 세 번째 클러스터
                    () -> assertThat(result.get(2).embeddingClusterId()).isEqualTo(cluster3.getId().intValue()),
                    () -> assertThat(result.get(2).label()).isEqualTo("세 번째 클러스터")
            );
        }

        @Test
        @DisplayName("limit 파라미터에 따라 제한된 개수의 클러스터를 조회한다")
        void findTopClusters_ReturnsLimitedClusters() {
            // given
            // 5개의 클러스터 생성
            final EmbeddingCluster cluster1 = embeddingClusterRepository.save(EmbeddingCluster.createEmpty());
            cluster1.updateLabel("클러스터1");
            embeddingClusterRepository.save(cluster1);

            final EmbeddingCluster cluster2 = embeddingClusterRepository.save(EmbeddingCluster.createEmpty());
            cluster2.updateLabel("클러스터2");
            embeddingClusterRepository.save(cluster2);

            final EmbeddingCluster cluster3 = embeddingClusterRepository.save(EmbeddingCluster.createEmpty());
            cluster3.updateLabel("클러스터3");
            embeddingClusterRepository.save(cluster3);

            final EmbeddingCluster cluster4 = embeddingClusterRepository.save(EmbeddingCluster.createEmpty());
            cluster4.updateLabel("클러스터4");
            embeddingClusterRepository.save(cluster4);

            final EmbeddingCluster cluster5 = embeddingClusterRepository.save(EmbeddingCluster.createEmpty());
            cluster5.updateLabel("클러스터5");
            embeddingClusterRepository.save(cluster5);

            // 각 클러스터에 피드백 추가 (5개, 4개, 3개, 2개, 1개)
            for (int i = 0; i < 5; i++) {
                createFeedbackWithCluster(organization, "클러스터1 피드백" + i, organizationCategory, cluster1);
            }
            for (int i = 0; i < 4; i++) {
                createFeedbackWithCluster(organization, "클러스터2 피드백" + i, organizationCategory, cluster2);
            }
            for (int i = 0; i < 3; i++) {
                createFeedbackWithCluster(organization, "클러스터3 피드백" + i, organizationCategory, cluster3);
            }
            for (int i = 0; i < 2; i++) {
                createFeedbackWithCluster(organization, "클러스터4 피드백" + i, organizationCategory, cluster4);
            }
            createFeedbackWithCluster(organization, "클러스터5 피드백1", organizationCategory, cluster5);

            // when - limit 3으로 상위 3개만 조회
            final List<ClusterInfo> result = feedbackRepository.findTopClusters(organization.getUuid(), 3);

            // then
            assertAll(
                    () -> assertThat(result).hasSize(3), // limit 3개만 반환
                    () -> assertThat(result.get(0).totalCount()).isEqualTo(5), // 가장 많은 피드백 수
                    () -> assertThat(result.get(0).embeddingClusterId()).isEqualTo(cluster1.getId().intValue()),
                    () -> assertThat(result.get(1).totalCount()).isEqualTo(4), // 두 번째로 많은 피드백 수
                    () -> assertThat(result.get(1).embeddingClusterId()).isEqualTo(cluster2.getId().intValue()),
                    () -> assertThat(result.get(2).totalCount()).isEqualTo(3), // 세 번째로 많은 피드백 수
                    () -> assertThat(result.get(2).embeddingClusterId()).isEqualTo(cluster3.getId().intValue())
            );
        }

        private void createFeedbackWithCluster(Organization organization, String content, 
                                               OrganizationCategory category, EmbeddingCluster cluster) {
            final Feedback feedback = FeedbackFixture.createFeedbackWithContent(organization, content, category);
            final Feedback savedFeedback = feedbackRepository.save(feedback);
            
            final FeedbackEmbeddingCluster feedbackCluster = FeedbackEmbeddingCluster.createNewCluster(
                    new double[]{0.1, 0.2, 0.3}, savedFeedback, cluster);
            feedbackEmbeddingClusterRepository.save(feedbackCluster);
        }
    }
}
