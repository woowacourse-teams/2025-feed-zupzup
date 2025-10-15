package feedzupzup.backend.feedback.application;

import static feedzupzup.backend.category.domain.Category.SUGGESTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.OrganizationCategoryRepository;
import feedzupzup.backend.category.fixture.OrganizationCategoryFixture;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Disabled("실제 OpenAI API 호출이 필요한 테스트 - 필요시에만 활성화")
class FeedbackClusteringServiceIntegrationTest extends ServiceIntegrationHelper {

    @Autowired
    private FeedbackClusteringService feedbackClusteringService;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationCategoryRepository organizationCategoryRepository;

    private Organization organization;
    private OrganizationCategory organizationCategory;

    @BeforeEach
    void setUp() {
        organization = organizationRepository.save(OrganizationFixture.createAllBlackBox());
        organizationCategory = organizationCategoryRepository.save(
                OrganizationCategoryFixture.createOrganizationCategory(organization, SUGGESTION));
    }

    @Nested
    @DisplayName("실제 OpenAI API를 사용한 클러스터링 테스트")
    class RealAPIClusteringTest {

        @Test
        @DisplayName("유사한 내용의 피드백이 같은 클러스터로 묶인다")
        void cluster_SimilarContent_SameCluster() {
            // given
            // 첫 번째 피드백 - "음식이 맛있어요"
            final Feedback firstFeedback = FeedbackFixture.createFeedbackWithContent(
                    organization, "음식이 정말 맛있어요", organizationCategory);
            final Feedback savedFirstFeedback = feedbackRepository.save(firstFeedback);

            // 두 번째 피드백 - "요리가 정말 맛있습니다" (유사한 내용)
            final Feedback secondFeedback = FeedbackFixture.createFeedbackWithContent(
                    organization, "요리가 정말 맛있습니다", organizationCategory);
            final Feedback savedSecondFeedback = feedbackRepository.save(secondFeedback);

            // when
            feedbackClusteringService.cluster(savedFirstFeedback.getId());
            feedbackClusteringService.cluster(savedSecondFeedback.getId());

            // then
            final Feedback clusteredFirst = feedbackRepository.findById(savedFirstFeedback.getId()).orElseThrow();
            final Feedback clusteredSecond = feedbackRepository.findById(savedSecondFeedback.getId()).orElseThrow();

            assertAll(
                    () -> assertThat(clusteredFirst.getClustering()).isNotNull(),
                    () -> assertThat(clusteredSecond.getClustering()).isNotNull(),
                    () -> assertThat(clusteredFirst.getClustering().clusterId())
                            .isEqualTo(clusteredSecond.getClustering().clusterId()),
                    () -> assertThat(clusteredSecond.getClustering().similarityScore()).isGreaterThan(0.7)
            );
        }

        @Test
        @DisplayName("다른 내용의 피드백은 다른 클러스터로 분류된다")
        void cluster_DifferentContent_DifferentClusters() {
            // given
            // 첫 번째 피드백 - "음식이 맛있어요"
            final Feedback foodFeedback = FeedbackFixture.createFeedbackWithContent(
                    organization, "음식이 맛있어요", organizationCategory);
            final Feedback savedFoodFeedback = feedbackRepository.save(foodFeedback);

            // 두 번째 피드백 - "서비스가 빨라요" (완전히 다른 내용)
            final Feedback serviceFeedback = FeedbackFixture.createFeedbackWithContent(
                    organization, "서비스가 빨라요", organizationCategory);
            final Feedback savedServiceFeedback = feedbackRepository.save(serviceFeedback);

            // when
            feedbackClusteringService.cluster(savedFoodFeedback.getId());
            feedbackClusteringService.cluster(savedServiceFeedback.getId());

            // then
            final Feedback clusteredFood = feedbackRepository.findById(savedFoodFeedback.getId()).orElseThrow();
            final Feedback clusteredService = feedbackRepository.findById(savedServiceFeedback.getId()).orElseThrow();

            assertAll(
                    () -> assertThat(clusteredFood.getClustering()).isNotNull(),
                    () -> assertThat(clusteredService.getClustering()).isNotNull(),
                    () -> assertThat(clusteredFood.getClustering().clusterId())
                            .isNotEqualTo(clusteredService.getClustering().clusterId())
            );
        }
    }
}
