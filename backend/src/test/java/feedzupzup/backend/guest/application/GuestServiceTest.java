package feedzupzup.backend.guest.application;

import static feedzupzup.backend.category.domain.Category.SUGGESTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.OrganizationCategoryRepository;
import feedzupzup.backend.category.fixture.OrganizationCategoryFixture;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.feedback.application.FeedbackLikeService;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.dto.response.MyFeedbackListResponse;
import feedzupzup.backend.feedback.dto.response.UserFeedbackItem;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.global.util.CurrentDateTime;
import feedzupzup.backend.guest.domain.guest.Guest;
import feedzupzup.backend.guest.domain.guest.GuestRepository;
import feedzupzup.backend.guest.domain.write.WriteHistory;
import feedzupzup.backend.guest.domain.write.WriteHistoryRepository;
import feedzupzup.backend.guest.dto.GuestInfo;
import feedzupzup.backend.guest.dto.response.LikeHistoryResponse;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class GuestServiceTest extends ServiceIntegrationHelper {

    @Autowired
    private GuestService guestService;

    @Autowired
    private FeedbackLikeService feedbackLikeService;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizationCategoryRepository organizationCategoryRepository;

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private WriteHistoryRepository writeHistoryRepository;

    Organization organization;
    OrganizationCategory organizationCategory;

    @BeforeEach
    void init() {
        organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);
        organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);
    }

    @Nested
    @DisplayName("내가 쓴 글 조회 테스트")
    class GetMyFeedbacksTest {

        @Test
        @DisplayName("Guest가 작성한 피드백 목록을 성공적으로 조회한다")
        void getMyFeedbacks_success() {
            // given
            final Guest guest = createAndSaveRandomGuest();
            final Feedback feedback1 = createAndSaveFeedback();
            final Feedback feedback2 = createAndSaveFeedback();
            final Feedback feedback3 = createAndSaveFeedback();
            createAndSaveWriteHistory(guest, feedback1);
            createAndSaveWriteHistory(guest, feedback2);
            createAndSaveWriteHistory(guest, feedback3);

            // when
            final MyFeedbackListResponse response = guestService.getMyFeedbackPage(
                    organization.getUuid(), toGuestInfo(guest));

            // then
            assertAll(
                    () -> assertThat(response.feedbacks()).hasSize(3),
                    () -> assertThat(response.feedbacks())
                            .extracting(UserFeedbackItem::feedbackId)
                            .containsExactlyInAnyOrder(feedback1.getId(), feedback2.getId(), feedback3.getId())
            );
        }

        @Test
        @DisplayName("특정 Organization의 피드백만 조회한다")
        void getMyFeedbacks_only_specific_organization() {
            // given
            final Organization otherOrganization = OrganizationFixture.createAllBlackBox();
            organizationRepository.save(otherOrganization);

            final OrganizationCategory organizationCategory1 = OrganizationCategoryFixture.createOrganizationCategory(
                    organization, SUGGESTION);
            final OrganizationCategory organizationCategory2 = OrganizationCategoryFixture.createOrganizationCategory(
                    otherOrganization, SUGGESTION);

            organizationCategoryRepository.save(organizationCategory1);
            organizationCategoryRepository.save(organizationCategory2);

            final Guest guest = createAndSaveRandomGuest();

            final Feedback targetFeedback1 = FeedbackFixture.createFeedbackWithOrganization(
                    organization, organizationCategory1);
            final Feedback targetFeedback2 = FeedbackFixture.createFeedbackWithOrganization(
                    organization, organizationCategory1);
            final Feedback otherFeedback = FeedbackFixture.createFeedbackWithOrganization(
                    otherOrganization, organizationCategory2);

            feedbackRepository.save(targetFeedback1);
            feedbackRepository.save(targetFeedback2);
            feedbackRepository.save(otherFeedback);

            createAndSaveWriteHistory(guest, targetFeedback1);
            createAndSaveWriteHistory(guest, targetFeedback2);
            createAndSaveWriteHistory(guest, otherFeedback);

            // when
            final MyFeedbackListResponse response = guestService.getMyFeedbackPage(
                    organization.getUuid(), toGuestInfo(guest));

            // then
            assertAll(
                    () -> assertThat(response.feedbacks()).hasSize(2),
                    () -> assertThat(response.feedbacks())
                            .extracting(UserFeedbackItem::feedbackId)
                            .containsExactlyInAnyOrder(targetFeedback1.getId(), targetFeedback2.getId()),
                    () -> assertThat(response.feedbacks())
                            .extracting(UserFeedbackItem::feedbackId)
                            .doesNotContain(otherFeedback.getId())
            );
        }

        @Test
        @DisplayName("다른 Guest가 작성한 피드백은 조회되지 않는다")
        void getMyFeedbacks_only_my_feedbacks() {
            // given
            final Guest guest = createAndSaveRandomGuest();
            final Guest otherGuest = createAndSaveRandomGuest();

            final Feedback feedback1 = createAndSaveFeedback();
            final Feedback feedback2 = createAndSaveFeedback();
            final Feedback feedback3 = createAndSaveFeedback();

            createAndSaveWriteHistory(guest, feedback1);
            createAndSaveWriteHistory(guest, feedback2);
            createAndSaveWriteHistory(otherGuest, feedback3);

            // when
            final MyFeedbackListResponse response = guestService.getMyFeedbackPage(
                    organization.getUuid(), toGuestInfo(guest));

            // then
            assertAll(
                    () -> assertThat(response.feedbacks()).hasSize(2),
                    () -> assertThat(response.feedbacks())
                            .extracting(UserFeedbackItem::feedbackId)
                            .containsExactlyInAnyOrder(feedback1.getId(), feedback2.getId()),
                    () -> assertThat(response.feedbacks())
                            .extracting(UserFeedbackItem::feedbackId)
                            .doesNotContain(feedback3.getId())
            );
        }

        @Test
        @DisplayName("작성한 피드백이 없는 경우 빈 목록을 반환한다")
        void getMyFeedbacks_empty_result() {
            // given
            final Organization organization = OrganizationFixture.createAllBlackBox();
            organizationRepository.save(organization);

            final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                    organization, SUGGESTION);
            organizationCategoryRepository.save(organizationCategory);

            final Guest guest = new Guest(UUID.randomUUID(), LocalDateTime.now());
            guestRepository.save(guest);

            // when
            final MyFeedbackListResponse response = guestService.getMyFeedbackPage(
                    organization.getUuid(), toGuestInfo(guest));

            // then
            assertThat(response.feedbacks()).isEmpty();
        }
    }

    @Nested
    @DisplayName("좋아요를 누른 피드백 조회 테스트")
    class LikeHistoryTest {

        @Test
        @DisplayName("좋아요 누른 기록이 없다면, 빈 배열이 반환되어야 한다.")
        void not_like_history_then_empty() {
            final LikeHistoryResponse likeHistories = guestService.findGuestLikeHistories(organization.getUuid(), toGuestInfo(createAndSaveRandomGuest()));
            assertThat(likeHistories.feedbackIds()).isEmpty();
        }

        @Test
        @DisplayName("좋아요 누른 기록을 전부 조회할 수 있어야 한다.")
        void find_all_histories() {
            // given
            final Feedback feedback = createAndSaveFeedback();
            final Guest guest = createAndSaveRandomGuest();

            feedbackLikeService.like(feedback.getId(), toGuestInfo(guest));

            // when
            final LikeHistoryResponse likeHistories = guestService.findGuestLikeHistories(organization.getUuid(), toGuestInfo(guest));

            // then
            assertAll(
                    () -> assertThat(likeHistories.feedbackIds().size()).isEqualTo(1),
                    () -> assertThat(likeHistories.feedbackIds().get(0)).isEqualTo(feedback.getId())
            );
        }
    }

    private GuestInfo toGuestInfo(Guest guest) {
        return new GuestInfo(guest.getGuestUuid());
    }

    private void createAndSaveWriteHistory(Guest guest, Feedback feedback) {
        final WriteHistory writeHistory = new WriteHistory(guest, feedback);
        writeHistoryRepository.save(writeHistory);
    }

    private Guest createAndSaveRandomGuest() {
        final Guest guest = new Guest(UUID.randomUUID(), CurrentDateTime.create());
        return guestRepository.save(guest);
    }

    private Feedback createAndSaveFeedback() {
        final Feedback feedback = FeedbackFixture.createFeedbackWithOrganization(organization, organizationCategory);
        return feedbackRepository.save(feedback);
    }
}
