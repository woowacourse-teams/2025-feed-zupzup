package feedzupzup.backend.feedback.application;

import static feedzupzup.backend.category.domain.Category.SUGGESTION;
import static feedzupzup.backend.feedback.domain.vo.FeedbackSortType.LATEST;
import static feedzupzup.backend.feedback.domain.vo.ProcessStatus.WAITING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.OrganizationCategoryRepository;
import feedzupzup.backend.category.fixture.OrganizationCategoryFixture;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.domain.vo.Content;
import feedzupzup.backend.feedback.domain.vo.LikeCount;
import feedzupzup.backend.feedback.domain.vo.PostedAt;
import feedzupzup.backend.feedback.domain.vo.UserName;
import feedzupzup.backend.feedback.dto.response.UserFeedbackItem;
import feedzupzup.backend.feedback.dto.response.UserFeedbackListResponse;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.global.util.CurrentDateTime;
import feedzupzup.backend.guest.domain.guest.Guest;
import feedzupzup.backend.guest.domain.guest.GuestRepository;
import feedzupzup.backend.guest.domain.write.WriteHistory;
import feedzupzup.backend.guest.domain.write.WriteHistoryRepository;
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

    @Autowired
    private WriteHistoryRepository writeHistoryRepository;

    private final Guest guest = new Guest(UUID.randomUUID(), CurrentDateTime.create());

    @BeforeEach
    void init() {
        guestRepository.save(guest);
    }

    @Nested
    @DisplayName("비밀글 마스킹 테스트")
    class SecretFeedbackMaskingTest {

        @Test
        @DisplayName("비밀글이고 본인이 작성하지 않은 경우 내용이 마스킹된다")
        void when_secret_feedback_and_not_owner_then_content_is_masked() {
            // given
            final Organization organization = OrganizationFixture.createAllBlackBox();
            organizationRepository.save(organization);

            final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                    organization, SUGGESTION);
            organizationCategoryRepository.save(organizationCategory);

            final Feedback secretFeedback = FeedbackFixture.createFeedbackWithSecret(
                    organization, true, organizationCategory);
            final Feedback savedSecretFeedback = feedBackRepository.save(secretFeedback);

            // when - 다른 게스트로 조회
            final Guest otherGuest = new Guest(UUID.randomUUID(), CurrentDateTime.create());
            guestRepository.save(otherGuest);

            final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(
                    organization.getUuid(), 10, null, null, LATEST, toGuestInfo(otherGuest));

            // then
            assertAll(
                    () -> assertThat(response.feedbacks()).hasSize(1),
                    () -> assertThat(response.feedbacks().get(0).feedbackId()).isEqualTo(savedSecretFeedback.getId()),
                    () -> assertThat(response.feedbacks().get(0).content()).isEqualTo("비밀글 입니다."),
                    () -> assertThat(response.feedbacks().get(0).isSecret()).isTrue()
            );
        }

        @Test
        @DisplayName("비밀글이지만 본인이 작성한 경우 원본 내용이 보인다")
        void when_secret_feedback_and_owner_then_content_is_visible() {
            // given
            final Organization organization = OrganizationFixture.createAllBlackBox();
            organizationRepository.save(organization);

            final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                    organization, SUGGESTION);
            organizationCategoryRepository.save(organizationCategory);

            final String originalContent = "이것은 비밀 내용입니다";
            final Feedback secretFeedback = Feedback.builder()
                    .content(new Content(originalContent))
                    .isSecret(true)
                    .status(WAITING)
                    .organization(organization)
                    .likeCount(new LikeCount(0))
                    .userName(new UserName("작성자"))
                    .postedAt(new PostedAt(CurrentDateTime.create()))
                    .organizationCategory(organizationCategory)
                    .build();
            final Feedback savedSecretFeedback = feedBackRepository.save(secretFeedback);

            // WriteHistory 생성 (본인이 작성했음을 나타냄)
            writeHistoryRepository.save(new WriteHistory(guest, savedSecretFeedback));

            // when - 작성자 본인으로 조회
            final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(
                    organization.getUuid(), 10, null, null, LATEST, toGuestInfo(guest));

            // then
            assertAll(
                    () -> assertThat(response.feedbacks()).hasSize(1),
                    () -> assertThat(response.feedbacks().get(0).feedbackId()).isEqualTo(savedSecretFeedback.getId()),
                    () -> assertThat(response.feedbacks().get(0).content()).isEqualTo(originalContent),
                    () -> assertThat(response.feedbacks().get(0).isSecret()).isTrue()
            );
        }

        @Test
        @DisplayName("비밀글이 아닌 경우 모든 사용자가 내용을 볼 수 있다")
        void when_not_secret_feedback_then_content_is_visible_to_everyone() {
            // given
            final Organization organization = OrganizationFixture.createAllBlackBox();
            organizationRepository.save(organization);

            final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                    organization, SUGGESTION);
            organizationCategoryRepository.save(organizationCategory);

            final String originalContent = "공개 피드백 내용";
            final Feedback publicFeedback = Feedback.builder()
                    .content(new Content(originalContent))
                    .isSecret(false)
                    .status(WAITING)
                    .organization(organization)
                    .likeCount(new LikeCount(0))
                    .userName(new UserName("작성자"))
                    .postedAt(new PostedAt(CurrentDateTime.create()))
                    .organizationCategory(organizationCategory)
                    .build();
            final Feedback savedPublicFeedback = feedBackRepository.save(publicFeedback);

            // when - 다른 게스트로 조회
            final Guest otherGuest = new Guest(UUID.randomUUID(), CurrentDateTime.create());
            guestRepository.save(otherGuest);

            final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(
                    organization.getUuid(), 10, null, null, LATEST, toGuestInfo(otherGuest));

            // then
            assertAll(
                    () -> assertThat(response.feedbacks()).hasSize(1),
                    () -> assertThat(response.feedbacks().get(0).feedbackId()).isEqualTo(savedPublicFeedback.getId()),
                    () -> assertThat(response.feedbacks().get(0).content()).isEqualTo(originalContent),
                    () -> assertThat(response.feedbacks().get(0).isSecret()).isFalse()
            );
        }

        @Test
        @DisplayName("비밀글과 공개글이 섞여있을 때 적절하게 마스킹된다")
        void when_mixed_secret_and_public_feedbacks_then_masked_correctly() {
            // given
            final Organization organization = OrganizationFixture.createAllBlackBox();
            organizationRepository.save(organization);

            final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                    organization, SUGGESTION);
            organizationCategoryRepository.save(organizationCategory);

            // 본인 비밀글
            final Feedback mySecretFeedback = Feedback.builder()
                    .content(new Content("내 비밀 피드백"))
                    .isSecret(true)
                    .status(WAITING)
                    .organization(organization)
                    .likeCount(new LikeCount(0))
                    .userName(new UserName("나"))
                    .postedAt(new PostedAt(CurrentDateTime.create()))
                    .organizationCategory(organizationCategory)
                    .build();
            final Feedback savedMySecretFeedback = feedBackRepository.save(mySecretFeedback);
            writeHistoryRepository.save(new WriteHistory(guest, savedMySecretFeedback));

            // 다른 사람 비밀글
            final Feedback otherSecretFeedback = Feedback.builder()
                    .content(new Content("다른 사람 비밀 피드백"))
                    .isSecret(true)
                    .status(WAITING)
                    .organization(organization)
                    .likeCount(new LikeCount(0))
                    .userName(new UserName("다른 사람"))
                    .postedAt(new PostedAt(CurrentDateTime.create()))
                    .organizationCategory(organizationCategory)
                    .build();
            final Feedback savedOtherSecretFeedback = feedBackRepository.save(otherSecretFeedback);

            // 공개글
            final Feedback publicFeedback = Feedback.builder()
                    .content(new Content("공개 피드백"))
                    .isSecret(false)
                    .status(WAITING)
                    .organization(organization)
                    .likeCount(new LikeCount(0))
                    .userName(new UserName("누군가"))
                    .postedAt(new PostedAt(CurrentDateTime.create()))
                    .organizationCategory(organizationCategory)
                    .build();
            final Feedback savedPublicFeedback = feedBackRepository.save(publicFeedback);

            // when
            final UserFeedbackListResponse response = userFeedbackService.getFeedbackPage(
                    organization.getUuid(), 10, null, null, LATEST, toGuestInfo(guest));

            final UserFeedbackItem publicItem = response.feedbacks().stream()
                    .filter(item -> item.feedbackId().equals(savedPublicFeedback.getId()))
                    .findFirst()
                    .orElseThrow();

            final UserFeedbackItem mySecretItem = response.feedbacks().stream()
                    .filter(item -> item.feedbackId().equals(savedMySecretFeedback.getId()))
                    .findFirst()
                    .orElseThrow();

            final UserFeedbackItem otherSecretItem = response.feedbacks().stream()
                    .filter(item -> item.feedbackId().equals(savedOtherSecretFeedback.getId()))
                    .findFirst()
                    .orElseThrow();


            // then
            assertAll(
                    () -> assertThat(response.feedbacks()).hasSize(3),
                    () -> assertThat(publicItem.content()).isEqualTo("공개 피드백"),
                    () -> assertThat(mySecretItem.content()).isEqualTo("내 비밀 피드백"),
                    () -> assertThat(otherSecretItem.content()).isEqualTo("비밀글 입니다.")

            );
        }
    }

    private GuestInfo toGuestInfo(Guest guest) {
        return new GuestInfo(guest.getGuestUuid());
    }
}
