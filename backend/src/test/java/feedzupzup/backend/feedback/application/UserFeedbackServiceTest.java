package feedzupzup.backend.feedback.application;

import static feedzupzup.backend.category.domain.Category.SUGGESTION;
import static feedzupzup.backend.feedback.domain.vo.FeedbackOrderBy.LATEST;
import static feedzupzup.backend.feedback.domain.vo.ProcessStatus.CONFIRMED;
import static feedzupzup.backend.feedback.domain.vo.ProcessStatus.WAITING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.OrganizationCategoryRepository;
import feedzupzup.backend.category.fixture.OrganizationCategoryFixture;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackLikeRepository;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.domain.vo.FeedbackOrderBy;
import feedzupzup.backend.feedback.dto.request.CreateFeedbackRequest;
import feedzupzup.backend.feedback.dto.response.CreateFeedbackResponse;
import feedzupzup.backend.feedback.dto.response.FeedbackItem;
import feedzupzup.backend.feedback.dto.response.UserFeedbackListResponse;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserFeedbackServiceTest extends ServiceIntegrationHelper {

    @Autowired
    private UserFeedbackService userFeedbackService;

    @Autowired
    private FeedbackRepository feedBackRepository;

    @Autowired
    private FeedbackLikeRepository feedbackLikeRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private FeedbackLikeService feedbackLikeService;

    @Autowired
    private OrganizationCategoryRepository organizationCategoryRepository;

    @BeforeEach
    void clear() {
        feedbackLikeRepository.clear();
    }

    @Test
    @DisplayName("피드백을 성공적으로 생성한다")
    void create_success() {
        //given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);
        final OrganizationCategory organizationCategory = new OrganizationCategory(organization, SUGGESTION, true);
        organizationCategoryRepository.save(organizationCategory);

        final CreateFeedbackRequest request = new CreateFeedbackRequest("맛있어요", false, "윌슨", "건의");

        //when
        final Organization savedOrganization = organizationRepository.save(organization);
        final CreateFeedbackResponse response = userFeedbackService.create(
                request, savedOrganization.getUuid());

        //then
        assertAll(
                () -> assertThat(response.feedbackId()).isNotNull(),
                () -> assertThat(response.content()).isEqualTo(request.content()),
                () -> assertThat(response.isSecret()).isEqualTo(request.isSecret()),
                () -> assertThat(response.postedAt()).isNotNull()
        );
    }

    @Test
    @DisplayName("organization에 존재하지 않는 카테고리가 주어졌을 때, 예외가 발생해야 한다.")
    void given_not_exist_category_in_organization_then_throw_exception() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        final OrganizationCategory organizationCategory1 = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationRepository.save(organization);
        organizationCategoryRepository.save(organizationCategory1);

        final CreateFeedbackRequest request = new CreateFeedbackRequest("맛있어요", false, "윌슨", "기타");

        // when & then
        assertThatThrownBy(() -> userFeedbackService.create(request, organization.getUuid()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("커서 기반 페이징으로 피드백 목록을 성공적으로 조회한다")
    void getFeedbackPage_success() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationRepository.save(organization);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback1 = FeedbackFixture.createFeedbackWithOrganization(
                organization, organizationCategory);
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithOrganization(
                organization, organizationCategory);
        final Feedback feedback3 = FeedbackFixture.createFeedbackWithOrganization(
                organization, organizationCategory);
        final Feedback feedback4 = FeedbackFixture.createFeedbackWithOrganization(
                organization, organizationCategory);

        feedBackRepository.save(feedback1);
        feedBackRepository.save(feedback2);
        feedBackRepository.save(feedback3);
        feedBackRepository.save(feedback4);

        final int size = 2;

        // when
        final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(
                organization.getUuid(), size, null, null, LATEST);

        // then
        assertAll(
                () -> assertThat(response.feedbacks()).hasSize(size),
                () -> assertThat(response.hasNext()).isTrue(),
                () -> assertThat(response.nextCursorId()).isNotNull()
        );
    }

    @Test
    @DisplayName("마지막 페이지에서 hasNext가 false를 반환한다")
    void getFeedbackPage_last_page() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback1 = FeedbackFixture.createFeedbackWithOrganization(
                organization,
                organizationCategory);
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithOrganization(
                organization,
                organizationCategory);
        feedBackRepository.save(feedback1);
        feedBackRepository.save(feedback2);

        final int size = 5;

        // when
        final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(
                organization.getUuid(), size, null, null, LATEST);

        // then
        assertAll(
                () -> assertThat(response.feedbacks()).hasSize(2),
                () -> assertThat(response.hasNext()).isFalse()
        );
    }

    @Test
    @DisplayName("빈 결과에 대해 적절히 처리한다")
    void getFeedbackPage_empty_result() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        final int size = 10;

        // when
        final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(
                organization.getUuid(), size, null, null, LATEST);
        // then
        assertAll(
                () -> assertThat(response.feedbacks()).isEmpty(),
                () -> assertThat(response.hasNext()).isFalse(),
                () -> assertThat(response.nextCursorId()).isNull()
        );
    }

    @Test
    @DisplayName("특정 커서 ID로 다음 페이지를 조회한다")
    void getFeedbackPage_with_cursor() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback1 = FeedbackFixture.createFeedbackWithOrganization(
                organization,
                organizationCategory);
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithOrganization(
                organization,
                organizationCategory);
        final Feedback feedback3 = FeedbackFixture.createFeedbackWithOrganization(
                organization,
                organizationCategory);
        final Feedback feedback4 = FeedbackFixture.createFeedbackWithOrganization(
                organization,
                organizationCategory);

        final Feedback saved1 = feedBackRepository.save(feedback1);
        final Feedback saved2 = feedBackRepository.save(feedback2);
        final Feedback saved3 = feedBackRepository.save(feedback3);
        feedBackRepository.save(feedback4);

        final int size = 2;
        final Long cursorId = saved3.getId(); // saved3를 커서로 사용하면 saved2, saved1이 반환됨

        // when
        final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(
                organization.getUuid(), size, cursorId, null, LATEST);

        // then
        assertAll(
                () -> assertThat(response.feedbacks()).hasSize(2),
                () -> assertThat(response.feedbacks().get(0).feedbackId()).isEqualTo(
                        saved2.getId()), // DESC 정렬이므로 saved2가 먼저
                () -> assertThat(response.feedbacks().get(1).feedbackId()).isEqualTo(
                        saved1.getId()),
                () -> assertThat(response.hasNext()).isFalse()
        );
    }

    @Test
    @DisplayName("단일 페이지 결과에서 다음 페이지가 없음을 확인한다")
    void getFeedbackPage_single_page() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback = FeedbackFixture.createFeedbackWithOrganization(
                organization,
                organizationCategory);
        feedBackRepository.save(feedback);

        final int size = 10;

        // when
        final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(
                organization.getUuid(), size, null, null, LATEST);

        // then
        assertAll(
                () -> assertThat(response.feedbacks()).hasSize(1),
                () -> assertThat(response.hasNext()).isFalse(),
                () -> assertThat(response.nextCursorId()).isNotNull()
        );
    }

    @Test
    @DisplayName("특정 장소의 피드백만 조회한다")
    void getFeedbackPage_only_specific_organization() {
        // given
        final Organization targetOrganization = OrganizationFixture.createAllBlackBox();
        final Organization otherOrganization = OrganizationFixture.createAllBlackBox();

        organizationRepository.save(targetOrganization);
        organizationRepository.save(otherOrganization);

        final OrganizationCategory organizationCategory1 = OrganizationCategoryFixture.createOrganizationCategory(
                targetOrganization, SUGGESTION);
        final OrganizationCategory organizationCategory2 = OrganizationCategoryFixture.createOrganizationCategory(
                otherOrganization, SUGGESTION);

        organizationCategoryRepository.save(organizationCategory1);
        organizationCategoryRepository.save(organizationCategory2);

        final Feedback targetFeedback1 = FeedbackFixture.createFeedbackWithOrganization(
                targetOrganization, organizationCategory1);
        final Feedback targetFeedback2 = FeedbackFixture.createFeedbackWithOrganization(
                targetOrganization, organizationCategory1);
        final Feedback otherFeedback = FeedbackFixture.createFeedbackWithOrganization(
                otherOrganization, organizationCategory2);

        feedBackRepository.save(targetFeedback1);
        feedBackRepository.save(targetFeedback2);
        feedBackRepository.save(otherFeedback);

        final int size = 10;

        // when
        final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(
                targetOrganization.getUuid(), size, null, null, LATEST);

        // then
        assertAll(
                () -> assertThat(response.feedbacks()).hasSize(2),
                () -> assertThat(response.feedbacks())
                        .extracting(FeedbackItem::feedbackId)
                        .doesNotContain(otherFeedback.getId()),
                () -> assertThat(response.hasNext()).isFalse()
        );
    }

    @Test
    @DisplayName("ProcessStatus가 CONFIRMED 으로 주어졌을 때, CONFIRMED 상태의 피드백만 조회할 수 있어야 한다.")
    void when_given_process_status_confirmed_then_only_get_confirmed_status() {

        final Organization targetOrganization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(targetOrganization);
        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                targetOrganization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback1 = FeedbackFixture.createFeedbackWithStatus(targetOrganization, CONFIRMED,
                organizationCategory);
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithStatus(targetOrganization, CONFIRMED,
                organizationCategory);
        final Feedback feedback3 = FeedbackFixture.createFeedbackWithStatus(targetOrganization, CONFIRMED,
                organizationCategory);
        final Feedback feedback4 = FeedbackFixture.createFeedbackWithStatus(targetOrganization, WAITING,
                organizationCategory);
        final Feedback feedback5 = FeedbackFixture.createFeedbackWithStatus(targetOrganization, WAITING,
                organizationCategory);

        feedBackRepository.save(feedback1);
        feedBackRepository.save(feedback2);
        feedBackRepository.save(feedback3);
        feedBackRepository.save(feedback4);
        feedBackRepository.save(feedback5);

        final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(
                targetOrganization.getUuid(), 10, null, CONFIRMED, LATEST);
        assertThat(response.feedbacks().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("ProcessStatus가 WAITING 으로 주어졌을 때, WAITING 상태의 피드백만 조회할 수 있어야 한다.")
    void when_given_process_status_waiting_then_only_get_waiting_status() {
        final Organization targetOrganization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(targetOrganization);
        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                targetOrganization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback1 = FeedbackFixture.createFeedbackWithStatus(targetOrganization, CONFIRMED,
                organizationCategory);
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithStatus(targetOrganization, CONFIRMED,
                organizationCategory);
        final Feedback feedback3 = FeedbackFixture.createFeedbackWithStatus(targetOrganization, CONFIRMED,
                organizationCategory);
        final Feedback feedback4 = FeedbackFixture.createFeedbackWithStatus(targetOrganization, WAITING,
                organizationCategory);
        final Feedback feedback5 = FeedbackFixture.createFeedbackWithStatus(targetOrganization, WAITING,
                organizationCategory);

        feedBackRepository.save(feedback1);
        feedBackRepository.save(feedback2);
        feedBackRepository.save(feedback3);
        feedBackRepository.save(feedback4);
        feedBackRepository.save(feedback5);

        final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(
                targetOrganization.getUuid(), 10, null, WAITING, LATEST);
        assertThat(response.feedbacks().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("ProcessStatus가 null로 주어졌을 때, 모든 피드백을 조회할 수 있어야 한다.")
    void when_given_process_status_null_then_get_all_feedbacks() {
        final Organization targetOrganization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(targetOrganization);
        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                targetOrganization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback1 = FeedbackFixture.createFeedbackWithStatus(targetOrganization, CONFIRMED,
                organizationCategory);
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithStatus(targetOrganization, CONFIRMED,
                organizationCategory);
        final Feedback feedback3 = FeedbackFixture.createFeedbackWithStatus(targetOrganization, CONFIRMED,
                organizationCategory);
        final Feedback feedback4 = FeedbackFixture.createFeedbackWithStatus(targetOrganization, WAITING,
                organizationCategory);
        final Feedback feedback5 = FeedbackFixture.createFeedbackWithStatus(targetOrganization, WAITING,
                organizationCategory);

        feedBackRepository.save(feedback1);
        feedBackRepository.save(feedback2);
        feedBackRepository.save(feedback3);
        feedBackRepository.save(feedback4);
        feedBackRepository.save(feedback5);

        final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(
                targetOrganization.getUuid(), 10, null, null, LATEST);
        assertThat(response.feedbacks().size()).isEqualTo(5);
    }

    @Test
    @DisplayName("DB 좋아요 수와 인메모리 좋아요 수가 합산되어 응답에 반영된다")
    void getFeedbackPage_reflects_memory_likes() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback1 = FeedbackFixture.createFeedbackWithLikes(
                organization, organizationCategory,
                5); // DB에 5개 좋아요
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithLikes(
                organization, organizationCategory,
                3); // DB에 3개 좋아요
        final Feedback feedback3 = FeedbackFixture.createFeedbackWithLikes(
                organization, organizationCategory,
                0); // DB에 0개 좋아요

        final Feedback saved1 = feedBackRepository.save(feedback1);
        final Feedback saved2 = feedBackRepository.save(feedback2);
        final Feedback saved3 = feedBackRepository.save(feedback3);

        // 인메모리에 좋아요 추가
        feedbackLikeService.like(saved1.getId()); // 인메모리에 1개 추가 -> 총 6개
        feedbackLikeService.like(saved1.getId()); // 인메모리에 1개 더 추가 -> 총 7개
        feedbackLikeService.like(saved2.getId()); // 인메모리에 1개 추가 -> 총 4개
        feedbackLikeService.like(saved3.getId()); // 인메모리에 1개 추가 -> 총 1개
        feedbackLikeService.like(saved3.getId()); // 인메모리에 1개 더 추가 -> 총 2개
        feedbackLikeService.like(saved3.getId()); // 인메모리에 1개 더 추가 -> 총 3개

        final int size = 10;

        // when
        final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(
                organization.getUuid(), size, null, null, LATEST);

        // then - 좋아요 수가 DB + 인메모리 합산 값으로 반영되는지 확인
        assertAll(
                () -> assertThat(response.feedbacks()).hasSize(3),
                () -> {
                    final var feedbackItems = response.feedbacks();
                    final var feedback1Item = feedbackItems.stream()
                            .filter(item -> item.feedbackId().equals(saved1.getId()))
                            .findFirst().orElseThrow();
                    final var feedback2Item = feedbackItems.stream()
                            .filter(item -> item.feedbackId().equals(saved2.getId()))
                            .findFirst().orElseThrow();
                    final var feedback3Item = feedbackItems.stream()
                            .filter(item -> item.feedbackId().equals(saved3.getId()))
                            .findFirst().orElseThrow();

                    assertThat(feedback1Item.likeCount()).isEqualTo(7); // 5(DB) + 2(인메모리) = 7
                    assertThat(feedback2Item.likeCount()).isEqualTo(4); // 3(DB) + 1(인메모리) = 4
                    assertThat(feedback3Item.likeCount()).isEqualTo(3); // 0(DB) + 3(인메모리) = 3
                }
        );
    }

    @Test
    @DisplayName("인메모리 좋아요가 없는 피드백은 DB 좋아요 수만 반영된다")
    void getFeedbackPage_reflects_only_db_likes_when_no_memory_likes() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback1 = FeedbackFixture.createFeedbackWithLikes(
                organization, organizationCategory,
                10); // DB에 10개 좋아요
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithLikes(
                organization, organizationCategory,
                0);  // DB에 0개 좋아요

        final Feedback saved1 = feedBackRepository.save(feedback1);
        final Feedback saved2 = feedBackRepository.save(feedback2);

        final int size = 10;

        // when - 인메모리 좋아요 추가 없이 조회
        final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(
                organization.getUuid(), size, null, null, LATEST);

        // then - DB 좋아요 수만 반영되는지 확인
        assertAll(
                () -> assertThat(response.feedbacks()).hasSize(2),
                () -> {
                    final var feedbackItems = response.feedbacks();
                    final var feedback1Item = feedbackItems.stream()
                            .filter(item -> item.feedbackId().equals(saved1.getId()))
                            .findFirst().orElseThrow();
                    final var feedback2Item = feedbackItems.stream()
                            .filter(item -> item.feedbackId().equals(saved2.getId()))
                            .findFirst().orElseThrow();

                    assertThat(feedback1Item.likeCount()).isEqualTo(10); // DB 좋아요 수만
                    assertThat(feedback2Item.likeCount()).isZero(); // DB 좋아요 수만
                }
        );
    }

    @Test
    @DisplayName("인메모리에만 좋아요가 있는 피드백도 정상적으로 반영된다")
    void getFeedbackPage_reflects_only_memory_likes() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback = FeedbackFixture.createFeedbackWithLikes(
                organization, organizationCategory,
                0); // DB에 0개 좋아요

        final Feedback saved = feedBackRepository.save(feedback);

        // 인메모리에만 좋아요 추가
        for (int i = 0; i < 5; i++) {
            feedbackLikeService.like(saved.getId()); // 인메모리에 총 5개 추가
        }

        final int size = 10;

        // when
        final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(
                organization.getUuid(), size, null, null, LATEST);
        final FeedbackItem userFeedbackItem = response.feedbacks().getFirst();

        // then - 인메모리 좋아요 수만 반영되는지 확인
        assertAll(
                () -> assertThat(response.feedbacks()).hasSize(1),
                () -> assertThat(userFeedbackItem.likeCount()).isEqualTo(5)
        );
    }

    @Test
    @DisplayName("인메모리 좋아요 취소가 반영되어 총 좋아요 수가 감소한다")
    void getFeedbackPage_reflects_unlike_operations() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback = FeedbackFixture.createFeedbackWithLikes(
                organization, organizationCategory,
                8); // DB에 8개 좋아요

        final Feedback saved = feedBackRepository.save(feedback);

        // 인메모리에 좋아요 추가 후 일부 취소
        feedbackLikeService.like(saved.getId());    // +1 -> 총 9개
        feedbackLikeService.like(saved.getId());    // +1 -> 총 10개
        feedbackLikeService.like(saved.getId());    // +1 -> 총 11개
        feedbackLikeService.unLike(saved.getId());  // -1 -> 총 10개
        feedbackLikeService.unLike(saved.getId());  // -1 -> 총 9개

        final int size = 10;

        // when
        final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(
                organization.getUuid(), size, null, null, LATEST);
        final FeedbackItem userFeedbackItem = response.feedbacks().getFirst();

        // then - 좋아요 취소가 반영되어 정확한 수가 계산되는지 확인
        assertAll(
                () -> assertThat(response.feedbacks()).hasSize(1),
                () -> assertThat(userFeedbackItem.likeCount()).isEqualTo(9)
        );
    }

    @Nested
    @DisplayName("정렬 기능 테스트")
    class OrderingTest {

        @Test
        @DisplayName("최신순으로 조회된다")
        void getFeedbackPage_orders_by_latest() {
            // given
            final Organization organization = OrganizationFixture.createAllBlackBox();
            organizationRepository.save(organization);

            final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                    organization, SUGGESTION);
            organizationCategoryRepository.save(organizationCategory);

            final Feedback feedback1 = FeedbackFixture.createFeedbackWithOrganization(
                    organization, organizationCategory);
            final Feedback feedback2 = FeedbackFixture.createFeedbackWithOrganization(
                    organization, organizationCategory);
            final Feedback feedback3 = FeedbackFixture.createFeedbackWithOrganization(
                    organization, organizationCategory);

            final Feedback saved1 = feedBackRepository.save(feedback1);
            final Feedback saved2 = feedBackRepository.save(feedback2);
            final Feedback saved3 = feedBackRepository.save(feedback3);

            // when - LATEST로 정렬
            final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(
                    organization.getUuid(), 10, null, null, FeedbackOrderBy.LATEST);

            // then - 최신(ID가 큰) 순서로 정렬되어야 함
            assertAll(
                    () -> assertThat(response.feedbacks()).hasSize(3),
                    () -> assertThat(response.feedbacks().get(0).feedbackId()).isEqualTo(saved3.getId()),
                    () -> assertThat(response.feedbacks().get(1).feedbackId()).isEqualTo(saved2.getId()),
                    () -> assertThat(response.feedbacks().get(2).feedbackId()).isEqualTo(saved1.getId())
            );
        }

        @Test
        @DisplayName("오래된순으로 조회된다")
        void getFeedbackPage_orders_by_oldest() {
            // given
            final Organization organization = OrganizationFixture.createAllBlackBox();
            organizationRepository.save(organization);

            final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                    organization, SUGGESTION);
            organizationCategoryRepository.save(organizationCategory);

            final Feedback feedback1 = FeedbackFixture.createFeedbackWithOrganization(
                    organization, organizationCategory);
            final Feedback feedback2 = FeedbackFixture.createFeedbackWithOrganization(
                    organization, organizationCategory);
            final Feedback feedback3 = FeedbackFixture.createFeedbackWithOrganization(
                    organization, organizationCategory);

            final Feedback saved1 = feedBackRepository.save(feedback1);
            final Feedback saved2 = feedBackRepository.save(feedback2);
            final Feedback saved3 = feedBackRepository.save(feedback3);

            // when - OLDEST로 정렬
            final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(
                    organization.getUuid(), 10, null, null, FeedbackOrderBy.OLDEST);

            // then - 오래된(ID가 작은) 순서로 정렬되어야 함
            assertAll(
                    () -> assertThat(response.feedbacks()).hasSize(3),
                    () -> assertThat(response.feedbacks().get(0).feedbackId()).isEqualTo(saved1.getId()),
                    () -> assertThat(response.feedbacks().get(1).feedbackId()).isEqualTo(saved2.getId()),
                    () -> assertThat(response.feedbacks().get(2).feedbackId()).isEqualTo(saved3.getId())
            );
        }

        @Test
        @DisplayName("좋아요 많은 순으로 조회된다")
        void getFeedbackPage_orders_by_likes() {
            // given
            final Organization organization = OrganizationFixture.createAllBlackBox();
            organizationRepository.save(organization);

            final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                    organization, SUGGESTION);
            organizationCategoryRepository.save(organizationCategory);

            final Feedback feedback1 = FeedbackFixture.createFeedbackWithLikes(
                    organization, organizationCategory, 5);
            final Feedback feedback2 = FeedbackFixture.createFeedbackWithLikes(
                    organization, organizationCategory, 10);
            final Feedback feedback3 = FeedbackFixture.createFeedbackWithLikes(
                    organization, organizationCategory, 3);

            final Feedback saved1 = feedBackRepository.save(feedback1); // 좋아요 5개
            final Feedback saved2 = feedBackRepository.save(feedback2); // 좋아요 10개
            final Feedback saved3 = feedBackRepository.save(feedback3); // 좋아요 3개

            // when - LIKES로 정렬
            final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(
                    organization.getUuid(), 10, null, null, FeedbackOrderBy.LIKES);

            // then - 좋아요 많은 순서로 정렬되어야 함 (10, 5, 3)
            assertAll(
                    () -> assertThat(response.feedbacks()).hasSize(3),
                    () -> assertThat(response.feedbacks().get(0).feedbackId()).isEqualTo(saved2.getId()),
                    () -> assertThat(response.feedbacks().get(0).likeCount()).isEqualTo(10),
                    () -> assertThat(response.feedbacks().get(1).feedbackId()).isEqualTo(saved1.getId()),
                    () -> assertThat(response.feedbacks().get(1).likeCount()).isEqualTo(5),
                    () -> assertThat(response.feedbacks().get(2).feedbackId()).isEqualTo(saved3.getId()),
                    () -> assertThat(response.feedbacks().get(2).likeCount()).isEqualTo(3)
            );
        }
    }
}
