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

        @Test
        @DisplayName("긍정적인 음식 리뷰들이 하나의 클러스터로 묶인다")
        void cluster_PositiveFoodReviews_SameCluster() {
            // given
            final String[] positiveFoodReviews = {
                    "음식이 너무 맛있어요!",
                    "서비스가 훌륭합니다",
                    "맛이 최고예요",
                    "정말 맛있게 먹었습니다"
            };

            // 첫 번째 피드백으로 클러스터 생성
            final Feedback firstFeedback = FeedbackFixture.createFeedbackWithContent(
                    organization, positiveFoodReviews[0], organizationCategory);
            final Feedback savedFirstFeedback = feedbackRepository.save(firstFeedback);
            feedbackClusteringService.cluster(savedFirstFeedback.getId());

            final UUID firstClusterId = feedbackRepository.findById(savedFirstFeedback.getId())
                    .orElseThrow().getClustering().clusterId();

            // when
            for (int i = 1; i < positiveFoodReviews.length; i++) {
                final Feedback feedback = FeedbackFixture.createFeedbackWithContent(
                        organization, positiveFoodReviews[i], organizationCategory);
                final Feedback savedFeedback = feedbackRepository.save(feedback);
                feedbackClusteringService.cluster(savedFeedback.getId());
            }

            //then
            final List<Feedback> clusteredFeedback = feedbackRepository.findAllByClustering_ClusterId(firstClusterId);
            assertAll(
                    () -> assertThat(clusteredFeedback).hasSize(5),
                    () -> assertThat(clusteredFeedback)
                            .allSatisfy(feedback ->
                                    assertThat(feedback.getClustering().similarityScore()).isGreaterThan(0.7)
                            )
            );
        }

        @Test
        @DisplayName("영어와 한국어 유사 내용이 같은 클러스터로 묶인다")
        void cluster_MultiLanguageSimilarContent_SameCluster() {
            // given
            final Feedback koreanFeedback = FeedbackFixture.createFeedbackWithContent(
                    organization, "음식이 맛있어요", organizationCategory);
            final Feedback englishFeedback = FeedbackFixture.createFeedbackWithContent(
                    organization, "The food is delicious", organizationCategory);

            final Feedback savedKoreanFeedback = feedbackRepository.save(koreanFeedback);
            final Feedback savedEnglishFeedback = feedbackRepository.save(englishFeedback);

            // when
            feedbackClusteringService.cluster(savedKoreanFeedback.getId());
            feedbackClusteringService.cluster(savedEnglishFeedback.getId());

            // then
            final Feedback clusteredKorean = feedbackRepository.findById(savedKoreanFeedback.getId()).orElseThrow();
            final Feedback clusteredEnglish = feedbackRepository.findById(savedEnglishFeedback.getId()).orElseThrow();

            assertAll(
                    () -> assertThat(clusteredKorean.getClustering()).isNotNull(),
                    () -> assertThat(clusteredEnglish.getClustering()).isNotNull(),
                    () -> assertThat(clusteredKorean.getClustering().clusterId())
                            .isEqualTo(clusteredEnglish.getClustering().clusterId()),
                    () -> assertThat(clusteredEnglish.getClustering().similarityScore()).isGreaterThan(0.7)
            );
        }
    }
}
