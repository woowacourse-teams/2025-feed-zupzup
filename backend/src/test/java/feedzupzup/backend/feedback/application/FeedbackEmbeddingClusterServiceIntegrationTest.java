package feedzupzup.backend.feedback.application;

import static feedzupzup.backend.category.domain.Category.SUGGESTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.OrganizationCategoryRepository;
import feedzupzup.backend.category.fixture.OrganizationCategoryFixture;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackEmbeddingCluster;
import feedzupzup.backend.feedback.domain.FeedbackEmbeddingClusterRepository;
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

@Disabled("실제 API 호출이 필요한 테스트 - 필요시에만 활성화")
class FeedbackEmbeddingClusterServiceIntegrationTest extends ServiceIntegrationHelper {

    @Autowired
    private FeedbackClusteringService feedbackClusteringService;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private FeedbackEmbeddingClusterRepository feedbackEmbeddingClusterRepository;

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
    @DisplayName("실제 외부 API를 사용한 클러스터링 테스트")
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
                    organization, "요리가 맛있습니다", organizationCategory);
            final Feedback savedSecondFeedback = feedbackRepository.save(secondFeedback);

            // when
            FeedbackEmbeddingCluster firstCluster = feedbackClusteringService.cluster(savedFirstFeedback.getId());
            FeedbackEmbeddingCluster secondCluster = feedbackClusteringService.cluster(savedSecondFeedback.getId());

            // then
            assertAll(
                    () -> assertThat(firstCluster).isNotNull(),
                    () -> assertThat(secondCluster).isNotNull(),
                    () -> assertThat(firstCluster.getEmbeddingCluster().getId())
                            .isEqualTo(secondCluster.getEmbeddingCluster().getId()),
                    () -> assertThat(secondCluster.getSimilarityScore()).isGreaterThan(0.75)
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
            FeedbackEmbeddingCluster foodCluster = feedbackClusteringService.cluster(savedFoodFeedback.getId());
            FeedbackEmbeddingCluster serviceCluster = feedbackClusteringService.cluster(savedServiceFeedback.getId());

            // then
            assertAll(
                    () -> assertThat(foodCluster).isNotNull(),
                    () -> assertThat(serviceCluster).isNotNull(),
                    () -> assertThat(foodCluster.getEmbeddingCluster().getId())
                            .isNotEqualTo(serviceCluster.getEmbeddingCluster().getId())
            );
        }

        @Test
        @DisplayName("복잡하고 긴 텍스트도 정확히 클러스터링된다")
        void cluster_ComplexLongText_ProperClustering() {
            // given
            // 첫 번째 복잡한 리뷰 - 레스토랑 서비스 칭찬
            final Feedback complexRestaurantFeedback = FeedbackFixture.createFeedbackWithContent(
                    organization, 
                    "오늘 이 레스토랑에서 식사를 했는데 정말 만족스러웠습니다. 음식의 품질이 매우 뛰어났고, 특히 파스타의 소스가 진짜 맛있었어요. " +
                    "웨이터분들도 매우 친절하시고 서비스가 빠르고 정확했습니다. 분위기도 로맨틱하고 좋았어요. 다음에 또 오고 싶은 곳이네요. " +
                    "가격대비 퀄리티가 정말 좋다고 생각해요. 강력 추천합니다!", 
                    organizationCategory);
            final Feedback savedComplexRestaurantFeedback = feedbackRepository.save(complexRestaurantFeedback);

            // 두 번째 복잡한 리뷰 - 비슷한 레스토랑 경험 (유사한 내용)
            final Feedback similarRestaurantFeedback = FeedbackFixture.createFeedbackWithContent(
                    organization,
                    "어제 저녁에 이곳에서 디너를 했습니다. 음식 맛이 정말 훌륭했고 직원분들의 서비스도 최고였어요. " +
                    "특히 스파게티가 정말 맛있었고, 직원들이 매우 세심하게 케어해주셔서 기분 좋게 식사할 수 있었습니다. " +
                    "레스토랑 인테리어도 예쁘고 데이트하기 좋은 분위기였어요. 가성비도 나쁘지 않다고 생각해요. 재방문 의사 있습니다.",
                    organizationCategory);
            final Feedback savedSimilarRestaurantFeedback = feedbackRepository.save(similarRestaurantFeedback);

            // 세 번째 복잡한 리뷰 - 완전히 다른 주제 (IT 서비스)
            final Feedback complexITFeedback = FeedbackFixture.createFeedbackWithContent(
                    organization,
                    "이번에 새로 출시된 모바일 앱을 사용해봤는데 정말 혁신적이고 사용자 친화적이라고 생각합니다. " +
                    "UI/UX 디자인이 직관적이고 깔끔해서 처음 사용하는 사람도 쉽게 사용할 수 있을 것 같아요. " +
                    "특히 결제 시스템이 빠르고 안전하다는 느낌을 받았습니다. 개발팀의 노력이 느껴지는 퀄리티높은 서비스네요. " +
                    "다만 몇 가지 버그가 있어서 업데이트를 통해 개선되었으면 좋겠습니다. 전반적으로는 만족스럽습니다.",
                    organizationCategory);
            final Feedback savedComplexITFeedback = feedbackRepository.save(complexITFeedback);

            // when
            FeedbackEmbeddingCluster restaurantCluster1 = feedbackClusteringService.cluster(savedComplexRestaurantFeedback.getId());
            FeedbackEmbeddingCluster restaurantCluster2 = feedbackClusteringService.cluster(savedSimilarRestaurantFeedback.getId());
            FeedbackEmbeddingCluster itCluster = feedbackClusteringService.cluster(savedComplexITFeedback.getId());

            // then
            assertAll(
                    // 모든 피드백이 클러스터링되었는지 확인
                    () -> assertThat(restaurantCluster1).isNotNull(),
                    () -> assertThat(restaurantCluster2).isNotNull(),
                    () -> assertThat(itCluster).isNotNull(),
                    
                    // 유사한 레스토랑 리뷰들이 같은 클러스터로 묶였는지 확인
                    () -> assertThat(restaurantCluster1.getEmbeddingCluster().getId())
                            .isEqualTo(restaurantCluster2.getEmbeddingCluster().getId()),
                    
                    // IT 서비스 리뷰는 다른 클러스터로 분류되었는지 확인
                    () -> assertThat(restaurantCluster1.getEmbeddingCluster().getId())
                            .isNotEqualTo(itCluster.getEmbeddingCluster().getId()),
                    
                    // 유사도 점수가 임계값 이상인지 확인
                    () -> assertThat(restaurantCluster2.getSimilarityScore()).isGreaterThan(0.75)
            );
        }

        @Test
        @DisplayName("긍정적인 음식 리뷰들이 하나의 클러스터로 묶인다")
        void cluster_PositiveFoodReviews_SameCluster() {
            // given
            final String[] positiveFoodReviews = {
                    "음식이 너무 맛있어요!",
                    "맛이 최고예요",
                    "정말 맛있게 먹었습니다"
            };

            // 첫 번째 피드백으로 클러스터 생성
            final Feedback firstFeedback = FeedbackFixture.createFeedbackWithContent(
                    organization, positiveFoodReviews[0], organizationCategory);
            final Feedback savedFirstFeedback = feedbackRepository.save(firstFeedback);
            FeedbackEmbeddingCluster firstCluster = feedbackClusteringService.cluster(savedFirstFeedback.getId());

            final Long firstClusterId = firstCluster.getEmbeddingCluster().getId();

            // when
            for (int i = 1; i < positiveFoodReviews.length; i++) {
                final Feedback feedback = FeedbackFixture.createFeedbackWithContent(
                        organization, positiveFoodReviews[i], organizationCategory);
                final Feedback savedFeedback = feedbackRepository.save(feedback);
                feedbackClusteringService.cluster(savedFeedback.getId());
            }

            //then
            final List<FeedbackEmbeddingCluster> clusteredFeedbacks = feedbackEmbeddingClusterRepository.findAllByEmbeddingCluster(firstCluster.getEmbeddingCluster());
            assertAll(
                    () -> assertThat(clusteredFeedbacks).hasSize(3),
                    () -> assertThat(clusteredFeedbacks)
                            .allSatisfy(cluster ->
                                    assertThat(cluster.getSimilarityScore()).isGreaterThan(0.75)
                            )
            );
        }

        @Test
        @DisplayName("감정과 문맥이 다른 텍스트들의 클러스터링 테스트")
        void cluster_DifferentEmotionAndContext_ProperClustering() {
            // given
            // 긍정적인 음식 리뷰
            final Feedback positiveFoodFeedback = FeedbackFixture.createFeedbackWithContent(
                    organization,
                    "정말 맛있는 음식이었어요! 셰프님의 정성이 느껴지는 요리였고, 재료도 신선했습니다. " +
                    "특히 메인 요리가 완벽했고 디저트까지 모두 만족스러웠어요. 친구들에게도 추천하고 싶어요.",
                    organizationCategory);
            final Feedback savedPositiveFoodFeedback = feedbackRepository.save(positiveFoodFeedback);

            // 부정적인 음식 리뷰 (같은 주제, 다른 감정)
            final Feedback negativeFoodFeedback = FeedbackFixture.createFeedbackWithContent(
                    organization,
                    "음식이 별로였어요. 맛이 싱겁고 양도 적었습니다. 가격에 비해 품질이 떨어진다고 생각해요. " +
                    "서비스도 느리고 직원들이 불친절했어요. 다시 오고 싶지 않네요. 개선이 필요할 것 같습니다.",
                    organizationCategory);
            final Feedback savedNegativeFoodFeedback = feedbackRepository.save(negativeFoodFeedback);

            // 중립적인 교육 서비스 리뷰 (다른 주제)
            final Feedback neutralEducationFeedback = FeedbackFixture.createFeedbackWithContent(
                    organization,
                    "온라인 강의를 수강했습니다. 강사님의 설명은 이해하기 쉬웠고 커리큘럼도 체계적이었어요. " +
                    "다만 실습 시간이 좀 더 있었으면 좋겠고, 질문에 대한 답변이 조금 늦었습니다. " +
                    "전반적으로는 괜찮은 수준의 강의라고 생각합니다. 가격도 적당해요.",
                    organizationCategory);
            final Feedback savedNeutralEducationFeedback = feedbackRepository.save(neutralEducationFeedback);

            // when
            FeedbackEmbeddingCluster positiveFoodCluster = feedbackClusteringService.cluster(savedPositiveFoodFeedback.getId());
            FeedbackEmbeddingCluster negativeFoodCluster = feedbackClusteringService.cluster(savedNegativeFoodFeedback.getId());
            FeedbackEmbeddingCluster educationCluster = feedbackClusteringService.cluster(savedNeutralEducationFeedback.getId());

            // then
            assertAll(
                    // 모든 피드백이 클러스터링되었는지 확인
                    () -> assertThat(positiveFoodCluster).isNotNull(),
                    () -> assertThat(negativeFoodCluster).isNotNull(),
                    () -> assertThat(educationCluster).isNotNull(),
                    
                    // 같은 음식 주제지만 감정이 다른 리뷰들의 클러스터링 결과 확인
                    // (임베딩 모델에 따라 같거나 다를 수 있음)
                    () -> assertThat(positiveFoodCluster.getEmbeddingCluster().getId()).isNotNull(),
                    () -> assertThat(negativeFoodCluster.getEmbeddingCluster().getId()).isNotNull(),
                    
                    // 다른 주제(교육)는 확실히 다른 클러스터로 분류되어야 함
                    () -> assertThat(positiveFoodCluster.getEmbeddingCluster().getId())
                            .isNotEqualTo(educationCluster.getEmbeddingCluster().getId()),
                    () -> assertThat(negativeFoodCluster.getEmbeddingCluster().getId())
                            .isNotEqualTo(educationCluster.getEmbeddingCluster().getId())
            );
        }
    }
}
