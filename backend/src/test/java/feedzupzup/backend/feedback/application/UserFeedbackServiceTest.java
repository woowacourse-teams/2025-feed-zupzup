package feedzupzup.backend.feedback.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.feedback.domain.FeedBackRepository;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackLikeRepository;
import feedzupzup.backend.feedback.domain.ProcessStatus;
import feedzupzup.backend.feedback.dto.request.CreateFeedbackRequest;
import feedzupzup.backend.feedback.dto.response.CreateFeedbackResponse;
import feedzupzup.backend.feedback.dto.response.UserFeedbackListResponse;
import feedzupzup.backend.feedback.dto.response.UserFeedbackListResponse.UserFeedbackItem;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserFeedbackServiceTest extends ServiceIntegrationHelper {

    @Autowired
    private UserFeedbackService userFeedbackService;

    @Autowired
    private FeedBackRepository feedBackRepository;

    @Autowired
    private FeedbackLikeRepository feedbackLikeRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private FeedbackLikeService feedbackLikeService;

    @BeforeEach
    void clear() {
        feedbackLikeRepository.clear();
    }

    @Test
    @DisplayName("피드백을 성공적으로 생성한다")
    void create_success() {
            //given
            final Organization organization = new Organization("테스트장소");
            final CreateFeedbackRequest request = new CreateFeedbackRequest("맛있어요", false, "윌슨");

            //when
            final Organization savedOrganization = organizationRepository.save(organization);
            final CreateFeedbackResponse response = userFeedbackService.create(request, savedOrganization.getId());

            //then
            assertAll(
                    () -> assertThat(response.feedbackId()).isNotNull(),
                    () -> assertThat(response.content()).isEqualTo(request.content()),
                    () -> assertThat(response.isSecret()).isEqualTo(request.isSecret()),
                    () -> assertThat(response.createdAt()).isNotNull()
            );
        }

    @Test
    @DisplayName("커서 기반 페이징으로 피드백 목록을 성공적으로 조회한다")
    void getFeedbackPage_success() {
        // given
        final Long organizationId = 1L;
        final Feedback feedback1 = FeedbackFixture.createFeedbackWithOrganizationId(organizationId);
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithOrganizationId(organizationId);
        final Feedback feedback3 = FeedbackFixture.createFeedbackWithOrganizationId(organizationId);
        final Feedback feedback4 = FeedbackFixture.createFeedbackWithOrganizationId(organizationId);

        feedBackRepository.save(feedback1);
        feedBackRepository.save(feedback2);
        feedBackRepository.save(feedback3);
        feedBackRepository.save(feedback4);

        final int size = 2;

        // when
        final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(organizationId, size, null);

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
        final Long organizationId = 1L;
        final Feedback feedback1 = FeedbackFixture.createFeedbackWithOrganizationId(organizationId);
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithOrganizationId(organizationId);

        feedBackRepository.save(feedback1);
        feedBackRepository.save(feedback2);

        final int size = 5;

        // when
        final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(organizationId, size, null);

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
        final Long organizationId = 1L;
        final int size = 10;

        // when
        final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(organizationId, size, null);

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
        final Long organizationId = 1L;
        final Feedback feedback1 = FeedbackFixture.createFeedbackWithOrganizationId(organizationId);
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithOrganizationId(organizationId);
        final Feedback feedback3 = FeedbackFixture.createFeedbackWithOrganizationId(organizationId);
        final Feedback feedback4 = FeedbackFixture.createFeedbackWithOrganizationId(organizationId);

        final Feedback saved1 = feedBackRepository.save(feedback1);
        final Feedback saved2 = feedBackRepository.save(feedback2);
        final Feedback saved3 = feedBackRepository.save(feedback3);
        feedBackRepository.save(feedback4);

        final int size = 2;
        final Long cursorId = saved3.getId(); // saved3를 커서로 사용하면 saved2, saved1이 반환됨

        // when
        final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(organizationId, size, cursorId);

        // then
        assertAll(
                () -> assertThat(response.feedbacks()).hasSize(2),
                () -> assertThat(response.feedbacks().get(0).feedbackId()).isEqualTo(saved2.getId()), // DESC 정렬이므로 saved2가 먼저
                () -> assertThat(response.feedbacks().get(1).feedbackId()).isEqualTo(saved1.getId()),
                () -> assertThat(response.hasNext()).isFalse()
        );
    }

    @Test
    @DisplayName("단일 페이지 결과에서 다음 페이지가 없음을 확인한다")
    void getFeedbackPage_single_page() {
        // given
        final Long organizationId = 1L;
        final Feedback feedback = FeedbackFixture.createFeedbackWithOrganizationId(organizationId);
        feedBackRepository.save(feedback);

        final int size = 10;

        // when
        final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(organizationId, size, null);

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
        final Long targetOrganizationId = 1L;
        final Long otherOrganizationId = 2L;

        final Feedback targetFeedback1 = FeedbackFixture.createFeedbackWithOrganizationId(targetOrganizationId);
        final Feedback targetFeedback2 = FeedbackFixture.createFeedbackWithOrganizationId(targetOrganizationId);
        final Feedback otherFeedback = FeedbackFixture.createFeedbackWithOrganizationId(otherOrganizationId);

        feedBackRepository.save(targetFeedback1);
        feedBackRepository.save(targetFeedback2);
        feedBackRepository.save(otherFeedback);

        final int size = 10;

        // when
        final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(targetOrganizationId, size, null);

        // then
        assertAll(
                () -> assertThat(response.feedbacks()).hasSize(2),
                () -> assertThat(response.feedbacks())
                        .extracting(UserFeedbackListResponse.UserFeedbackItem::feedbackId)
                        .doesNotContain(otherFeedback.getId()),
                () -> assertThat(response.hasNext()).isFalse()
        );
    }

    @Test
    @DisplayName("DB 좋아요 수와 인메모리 좋아요 수가 합산되어 응답에 반영된다")
    void getFeedbackPage_reflects_memory_likes() {
        // given
        final Long organizationId = 1L;
        final Feedback feedback1 = FeedbackFixture.createFeedbackWithLikes(organizationId, 5); // DB에 5개 좋아요
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithLikes(organizationId, 3); // DB에 3개 좋아요
        final Feedback feedback3 = FeedbackFixture.createFeedbackWithLikes(organizationId, 0); // DB에 0개 좋아요

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
        final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(organizationId, size, null);

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
        final Long organizationId = 1L;
        final Feedback feedback1 = FeedbackFixture.createFeedbackWithLikes(organizationId, 10); // DB에 10개 좋아요
        final Feedback feedback2 = FeedbackFixture.createFeedbackWithLikes(organizationId, 0);  // DB에 0개 좋아요

        final Feedback saved1 = feedBackRepository.save(feedback1);
        final Feedback saved2 = feedBackRepository.save(feedback2);

        final int size = 10;

        // when - 인메모리 좋아요 추가 없이 조회
        final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(organizationId, size, null);

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
        final Long organizationId = 1L;
        final Feedback feedback = FeedbackFixture.createFeedbackWithLikes(organizationId, 0); // DB에 0개 좋아요

        final Feedback saved = feedBackRepository.save(feedback);

        // 인메모리에만 좋아요 추가
        for (int i = 0; i < 5; i++) {
            feedbackLikeService.like(saved.getId()); // 인메모리에 총 5개 추가
        }

        final int size = 10;

        // when
        final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(organizationId, size, null);
        final UserFeedbackItem userFeedbackItem = response.feedbacks().getFirst();

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
        final Long organizationId = 1L;
        final Feedback feedback = FeedbackFixture.createFeedbackWithLikes(organizationId, 8); // DB에 8개 좋아요

        final Feedback saved = feedBackRepository.save(feedback);

        // 인메모리에 좋아요 추가 후 일부 취소
        feedbackLikeService.like(saved.getId());    // +1 -> 총 9개
        feedbackLikeService.like(saved.getId());    // +1 -> 총 10개
        feedbackLikeService.like(saved.getId());    // +1 -> 총 11개
        feedbackLikeService.unLike(saved.getId());  // -1 -> 총 10개
        feedbackLikeService.unLike(saved.getId());  // -1 -> 총 9개

        final int size = 10;

        // when
        final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(organizationId, size, null);
        final UserFeedbackItem userFeedbackItem = response.feedbacks().getFirst();

        // then - 좋아요 취소가 반영되어 정확한 수가 계산되는지 확인
        assertAll(
                () -> assertThat(response.feedbacks()).hasSize(1),
                () -> assertThat(userFeedbackItem.likeCount()).isEqualTo(9)
        );
    }

    @Test
    @DisplayName("특정 날짜 이후의 피드백에 대한 통계를 계산한다")
    void calculateStatistic_success() {
        // given
        final Organization organization = new Organization("woowacourse");
        organizationRepository.save(organization);

        final Feedback confirmedFeedback1 = FeedbackFixture.createFeedbackWithStatus(
                ProcessStatus.CONFIRMED);
        final Feedback waitingFeedback = FeedbackFixture.createFeedbackWithStatus(
                ProcessStatus.WAITING);
        final Feedback confirmedFeedback2 = FeedbackFixture.createFeedbackWithStatus(
                ProcessStatus.CONFIRMED);

        // 피드백 저장
        feedBackRepository.save(confirmedFeedback1);
        feedBackRepository.save(waitingFeedback);
        feedBackRepository.save(confirmedFeedback2);

        final int daysAgo = 7; // 7일 전 이후의 피드백만 조회

        // when
        final StatisticResponse response = userFeedbackService.calculateStatistic(place.getId(),
                daysAgo);

        // then
        // 모든 피드백이 현재 날짜에 생성되므로 모두 포함됨
        assertAll(
                () -> assertThat(response.totalCount()).isEqualTo(3), // 모든 피드백 포함
                () -> assertThat(response.confirmedCount()).isEqualTo(2), // CONFIRMED 상태 피드백 2개
                () -> assertThat(response.waitingCount()).isEqualTo(1), // WAITING 상태 피드백 1개
                () -> assertThat(response.reflectionRate()).isEqualTo(66.67) // 2/3 * 100 = 66.67%
        );
    }

    @Test
    @DisplayName("통계 메서드 호출 중 존재하지 않는 place가 주어진다면, 예외가 발생해야 한다.")
    void calculateStatistic_withNoFeedbacks() {
        // given
        final Long organizationId = 999L; // 존재하지 않는 장소 ID
        final int daysAgo = 7;

        // when & then
        assertThatThrownBy(() -> userFeedbackService.calculateStatistic(organizationId, daysAgo))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Nested
    @DisplayName("period 값에 따라, 통계 메서드를 호출할 수 있어야 한다.")
    class CalculateStatisticWithVariousStatuses {

        @Test
        @DisplayName("당일 조회 테스트 케이스")
        void one_day() {
            final Organization organization = new Organization("woowacourse");
            organizationRepository.save(organization);
            // given
            final PostedAt postedAt1 = PostedAt.from(LocalDateTime.now().minusDays(2L)); // 이틀 전
            final PostedAt postedAt2 = PostedAt.from(LocalDateTime.now().minusDays(1L)); // 하루 전
            final PostedAt postedAt3 = PostedAt.from(LocalDateTime.now()); //오늘
            final Feedback feedback1 = FeedbackFixture.createFeedbackWithPostedAtAndStatus(
                    postedAt1, ProcessStatus.WAITING);
            final Feedback feedback2 = FeedbackFixture.createFeedbackWithPostedAtAndStatus(
                    postedAt2, ProcessStatus.WAITING);
            final Feedback feedback3 = FeedbackFixture.createFeedbackWithPostedAtAndStatus(
                    postedAt3, ProcessStatus.WAITING);

            feedBackRepository.save(feedback1);
            feedBackRepository.save(feedback2);
            feedBackRepository.save(feedback3);

            // when
            final StatisticResponse response = userFeedbackService.calculateStatistic(1L, 1);

            // then
            assertThat(response.totalCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("당일 조회 경곗값 케이스 테스트")
        void one_day_boundary_value() {
            // given
            final Organization organization = new Organization("woowacourse");
            organizationRepository.save(organization);

            // 당일 전날의 23:59시
            final LocalDateTime targetDate1 = LocalDate.now().atStartOfDay().minusMinutes(1);

            // 당일 00:00시
            final LocalDateTime targetDate2 = LocalDate.now().atStartOfDay();
            final LocalDateTime targetDate3 = LocalDate.now().atStartOfDay().plusMinutes(1);

            final PostedAt postedAt1 = PostedAt.from(targetDate1);
            final PostedAt postedAt2 = PostedAt.from(targetDate2);
            final PostedAt postedAt3 = PostedAt.from(targetDate3);
            final Feedback feedback1 = FeedbackFixture.createFeedbackWithPostedAtAndStatus(
                    postedAt1, ProcessStatus.WAITING);
            final Feedback feedback2 = FeedbackFixture.createFeedbackWithPostedAtAndStatus(
                    postedAt2, ProcessStatus.WAITING);
            final Feedback feedback3 = FeedbackFixture.createFeedbackWithPostedAtAndStatus(
                    postedAt3, ProcessStatus.WAITING);

            feedBackRepository.save(feedback1);
            feedBackRepository.save(feedback2);
            feedBackRepository.save(feedback3);

            // when
            final StatisticResponse response = userFeedbackService.calculateStatistic(1L, 1);

            // then
            assertThat(response.totalCount()).isEqualTo(2); //targetDate2, targetDate3
        }

        @Test
        @DisplayName("일주일 간의 피드백에 대한 통계 테스트")
        void once_a_week() {
            // given
            final Organization organization = new Organization("woowacourse");
            organizationRepository.save(organization);
            final PostedAt postedAt1 = PostedAt.from(LocalDateTime.now().minusDays(7L));
            final PostedAt postedAt2 = PostedAt.from(LocalDateTime.now().minusDays(6L));
            final Feedback feedback1 = FeedbackFixture.createFeedbackWithPostedAtAndStatus(
                    postedAt1, ProcessStatus.WAITING);
            final Feedback feedback2 = FeedbackFixture.createFeedbackWithPostedAtAndStatus(
                    postedAt2, ProcessStatus.WAITING);
            feedBackRepository.save(feedback1);
            feedBackRepository.save(feedback2);

            // when
            final StatisticResponse response = userFeedbackService.calculateStatistic(1L, 7);

            // then
            assertThat(response.totalCount()).isEqualTo(1);
        }
    }

}
