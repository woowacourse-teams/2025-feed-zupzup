package feedzupzup.backend.feedback.application;

import static feedzupzup.backend.category.domain.Category.SUGGESTION;
import static feedzupzup.backend.feedback.domain.vo.FeedbackSortType.LATEST;
import static feedzupzup.backend.feedback.domain.vo.FeedbackSortType.LIKES;
import static feedzupzup.backend.feedback.domain.vo.FeedbackSortType.OLDEST;
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
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.dto.request.CreateFeedbackRequest;
import feedzupzup.backend.feedback.dto.response.CreateFeedbackResponse;
import feedzupzup.backend.feedback.dto.response.UserFeedbackItem;
import feedzupzup.backend.feedback.dto.response.UserFeedbackListResponse;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.global.util.CurrentDateTime;
import feedzupzup.backend.guest.domain.guest.Guest;
import feedzupzup.backend.guest.domain.guest.GuestRepository;
import feedzupzup.backend.guest.dto.GuestInfo;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import java.util.UUID;
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
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationCategoryRepository organizationCategoryRepository;

    @Autowired
    private GuestRepository guestRepository;

    private final Guest guest = new Guest(UUID.randomUUID(), CurrentDateTime.create());

    @BeforeEach
    void init() {
        guestRepository.save(guest);
    }

    @Test
    @DisplayName("피드백을 성공적으로 생성한다")
    void create_success() {
        //given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);
        final OrganizationCategory organizationCategory = new OrganizationCategory(organization, SUGGESTION, true);
        organizationCategoryRepository.save(organizationCategory);

        final CreateFeedbackRequest request = new CreateFeedbackRequest(
                "맛있어요", false, "윌슨",
                "건의", "https://example.com/image.png");

        //when
        final Organization savedOrganization = organizationRepository.save(organization);
        final CreateFeedbackResponse response = userFeedbackService.create(
                request, savedOrganization.getUuid(), toGuestInfo(guest));

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

        final CreateFeedbackRequest request = new CreateFeedbackRequest(
                "맛있어요", false, "윌슨",
                "기타", "https://example.com/image.png");

        // when & then
        assertThatThrownBy(() -> userFeedbackService.create(request, organization.getUuid(), toGuestInfo(guest)))
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
                        .extracting(UserFeedbackItem::feedbackId)
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
                    organization.getUuid(), 10, null, null, LATEST);

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
                    organization.getUuid(), 10, null, null, OLDEST);

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
                    organization.getUuid(), 10, null, null, LIKES);

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

    private GuestInfo toGuestInfo(Guest guest) {
        return new GuestInfo(guest.getGuestUuid());
    }
}
