package feedzupzup.backend.feedback.application;

import static feedzupzup.backend.category.domain.Category.SUGGESTION;
import static feedzupzup.backend.feedback.domain.vo.FeedbackSortBy.LATEST;
import static feedzupzup.backend.feedback.domain.vo.FeedbackSortBy.LIKES;
import static feedzupzup.backend.feedback.domain.vo.FeedbackSortBy.OLDEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.admin.domain.fixture.AdminFixture;
import feedzupzup.backend.admin.domain.vo.AdminName;
import feedzupzup.backend.admin.domain.vo.LoginId;
import feedzupzup.backend.admin.domain.vo.Password;
import feedzupzup.backend.auth.exception.AuthException.ForbiddenException;
import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.OrganizationCategoryRepository;
import feedzupzup.backend.category.fixture.OrganizationCategoryFixture;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.domain.vo.FeedbackSortBy;
import feedzupzup.backend.feedback.domain.vo.ProcessStatus;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackCommentRequest;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackSecretRequest;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackStatusRequest;
import feedzupzup.backend.feedback.dto.response.AdminFeedbackListResponse;
import feedzupzup.backend.feedback.dto.response.UpdateFeedbackCommentResponse;
import feedzupzup.backend.feedback.dto.response.UpdateFeedbackSecretResponse;
import feedzupzup.backend.feedback.dto.response.UpdateFeedbackStatusResponse;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import feedzupzup.backend.organizer.domain.Organizer;
import feedzupzup.backend.organizer.domain.OrganizerRepository;
import feedzupzup.backend.organizer.domain.OrganizerRole;
import java.util.UUID;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AdminFeedbackServiceTest extends ServiceIntegrationHelper {

    @Autowired
    private AdminFeedbackService adminFeedbackService;

    @Autowired
    private FeedbackRepository feedBackRepository;

    @Autowired
    private OrganizationCategoryRepository organizationCategoryRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private OrganizerRepository organizerRepository;

    private Admin admin;
    @BeforeEach
    void setUpAuth() {
        admin = AdminFixture.create();
        adminRepository.save(admin);
    }

    @Nested
    @DisplayName("피드백 삭제 테스트")
    class DeleteFeedbackTest {

        @Test
        @DisplayName("피드백을 성공적으로 삭제한다")
        void delete_success() {
            // given
            final Organization organization = OrganizationFixture.createAllBlackBox();
            organizationRepository.save(organization);

            final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                    organization, SUGGESTION);
            organizationCategoryRepository.save(organizationCategory);

            final Feedback feedback = FeedbackFixture.createFeedbackWithOrganization(organization,
                    organizationCategory);
            final Feedback savedFeedback = feedBackRepository.save(feedback);

            // when
            adminFeedbackService.delete(savedFeedback.getId());

            // then
            assertThat(feedBackRepository.findById(savedFeedback.getId())).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 피드백 삭제 시 예외가 발생 하지 않는다.")
        void delete_non_existing_feedback_exception() {
            // given
            final Long nonExistingId = 999L;

            // when & then
            assertThatCode(() -> adminFeedbackService.delete(nonExistingId)).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("피드백 상태 업데이트 테스트")
    class UpdateFeedbackStatusTest {

        @Test
        @DisplayName("유효한 피드백 ID와 상태로 업데이트 시 성공한다")
        void updateFeedbackStatus_success() {
            // given
            final Organization organization = OrganizationFixture.createAllBlackBox();

            final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                    organization, SUGGESTION);
            organizationRepository.save(organization);
            organizationCategoryRepository.save(organizationCategory);

            final Feedback feedback = FeedbackFixture.createFeedbackWithStatus(organization, ProcessStatus.WAITING,
                    organizationCategory);
            final Feedback savedFeedback = feedBackRepository.save(feedback);
            final UpdateFeedbackStatusRequest request = new UpdateFeedbackStatusRequest(ProcessStatus.CONFIRMED);

            // when
            final UpdateFeedbackStatusResponse response = adminFeedbackService.updateFeedbackStatus(
                    request, savedFeedback.getId());

            // then
            assertThat(response).isNotNull();
            assertThat(response.status()).isEqualTo(ProcessStatus.CONFIRMED);
        }

        @Test
        @DisplayName("존재하지 않는 피드백 ID로 업데이트 시 예외가 발생한다")
        void updateFeedbackStatus_not_found() {
            // given
            final Long nonExistentFeedbackId = 999L;
            final UpdateFeedbackStatusRequest request = new UpdateFeedbackStatusRequest(ProcessStatus.CONFIRMED);

            // when & then
            assertThatThrownBy(() -> adminFeedbackService.updateFeedbackStatus(
                    request, nonExistentFeedbackId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("피드백 비밀상태 변경 테스트")
    class UpdateFeedbackSecretTest {

        @Test
        @DisplayName("유효한 피드백 ID와 비밀상태로 업데이트 시 성공한다")
        void updateFeedbackSecret_success() {
            // given
            final Organization organization = OrganizationFixture.createAllBlackBox();
            organizationRepository.save(organization);

            final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                    organization, SUGGESTION);
            organizationCategoryRepository.save(organizationCategory);

            final Feedback feedback = FeedbackFixture.createFeedbackWithSecret(organization, false,
                    organizationCategory);
            final Feedback savedFeedback = feedBackRepository.save(feedback);
            final UpdateFeedbackSecretRequest request = new UpdateFeedbackSecretRequest(true);

            // when
            final UpdateFeedbackSecretResponse response = adminFeedbackService.updateFeedbackSecret(
                    savedFeedback.getId(), request);

            // then
            assertAll(
                    () -> assertThat(response.feedbackId()).isEqualTo(savedFeedback.getId()),
                    () -> assertThat(response.isSecret()).isTrue()
            );
        }

        @Test
        @DisplayName("존재하지 않는 피드백 ID로 비밀상태 변경 시 예외가 발생한다")
        void updateFeedbackSecret_not_found() {
            // given
            final Long nonExistentFeedbackId = 999L;
            final UpdateFeedbackSecretRequest request = new UpdateFeedbackSecretRequest(true);

            // when & then
            assertThatThrownBy(() -> adminFeedbackService.updateFeedbackSecret(nonExistentFeedbackId, request))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("전체 피드백 조회 테스트")
    class GetAllFeedbacksTest {

        @Test
        @DisplayName("커서 기반 페이징으로 피드백 목록을 성공적으로 조회한다")
        void getAllFeedbacks_success() {
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
            final Feedback feedback4 = FeedbackFixture.createFeedbackWithOrganization(
                    organization, organizationCategory);

            feedBackRepository.save(feedback1);
            feedBackRepository.save(feedback2);
            feedBackRepository.save(feedback3);
            feedBackRepository.save(feedback4);

            final int size = 2;

            // when
            final AdminFeedbackListResponse response = adminFeedbackService.getFeedbackPage(
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
        void getAllFeedbacks_last_page() {
            // given
            final Organization organization = OrganizationFixture.createAllBlackBox();
            organizationRepository.save(organization);

            final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                    organization, SUGGESTION);
            organizationCategoryRepository.save(organizationCategory);

            final Feedback feedback1 = FeedbackFixture.createFeedbackWithOrganization(organization,
                    organizationCategory);
            final Feedback feedback2 = FeedbackFixture.createFeedbackWithOrganization(organization,
                    organizationCategory);

            feedBackRepository.save(feedback1);
            feedBackRepository.save(feedback2);

            final int size = 5;

            // when
            final AdminFeedbackListResponse response = adminFeedbackService.getFeedbackPage(
                    organization.getUuid(), size, null, null, LATEST);

            // then
            assertAll(
                    () -> assertThat(response.feedbacks()).hasSize(2),
                    () -> assertThat(response.hasNext()).isFalse()
            );
        }

        @Test
        @DisplayName("빈 결과에 대해 적절히 처리한다")
        void getAllFeedbacks_empty_result() {
            // given
            final UUID organizationUuid = UUID.randomUUID();
            final int size = 10;

            // when
            final AdminFeedbackListResponse response = adminFeedbackService.getFeedbackPage(
                    organizationUuid, size, null, null, LATEST);

            // then
            assertAll(
                    () -> assertThat(response.feedbacks()).isEmpty(),
                    () -> assertThat(response.hasNext()).isFalse(),
                    () -> assertThat(response.nextCursorId()).isNull()
            );
        }
    }

    @Nested
    @DisplayName("특정 장소 피드백 조회 테스트")
    class GetFeedbackPageByOrganizationIdTest {

        @Test
        @DisplayName("특정 장소의 피드백만 조회한다")
        void getFeedbackPageByOrganizationId_success() {
            // given
            final Organization targetOrganization = OrganizationFixture.createAllBlackBox();
            final Organization otherOrganization = OrganizationFixture.createAllBlackBox();

            organizationRepository.save(targetOrganization);
            organizationRepository.save(otherOrganization);

            final OrganizationCategory organizationCategory1 = OrganizationCategoryFixture.createOrganizationCategory(
                    targetOrganization, SUGGESTION);
            organizationCategoryRepository.save(organizationCategory1);

            final OrganizationCategory organizationCategory2 = OrganizationCategoryFixture.createOrganizationCategory(
                    targetOrganization, SUGGESTION);
            organizationCategoryRepository.save(organizationCategory2);

            final Feedback targetFeedback1 = FeedbackFixture.createFeedbackWithOrganization(targetOrganization,
                    organizationCategory1);
            final Feedback targetFeedback2 = FeedbackFixture.createFeedbackWithOrganization(targetOrganization,
                    organizationCategory1);
            final Feedback otherFeedback = FeedbackFixture.createFeedbackWithOrganization(otherOrganization,
                    organizationCategory2);

            feedBackRepository.save(targetFeedback1);
            feedBackRepository.save(targetFeedback2);
            feedBackRepository.save(otherFeedback);

            final int size = 10;

            // when
            final AdminFeedbackListResponse response = adminFeedbackService.getFeedbackPage(
                    targetOrganization.getUuid(), size, null, null, LATEST);

            // then
            assertAll(
                    () -> assertThat(response.feedbacks()).hasSize(2),
                    () -> assertThat(response.feedbacks())
                            .extracting(AdminFeedbackListResponse.AdminFeedbackItem::feedbackId)
                            .doesNotContain(otherFeedback.getId()),
                    () -> assertThat(response.hasNext()).isFalse()
            );
        }

        @Test
        @DisplayName("특정 장소의 피드백을 커서 기반 페이징으로 조회한다")
        void getFeedbackPageByOrganizationId_with_paging() {
            // given
            final Organization organization = OrganizationFixture.createAllBlackBox();
            organizationRepository.save(organization);

            final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                    organization, SUGGESTION);
            organizationCategoryRepository.save(organizationCategory);

            final Feedback feedback1 = FeedbackFixture.createFeedbackWithOrganization(organization,
                    organizationCategory);
            final Feedback feedback2 = FeedbackFixture.createFeedbackWithOrganization(organization,
                    organizationCategory);
            final Feedback feedback3 = FeedbackFixture.createFeedbackWithOrganization(organization,
                    organizationCategory);
            final Feedback feedback4 = FeedbackFixture.createFeedbackWithOrganization(organization,
                    organizationCategory);

            feedBackRepository.save(feedback1);
            feedBackRepository.save(feedback2);
            feedBackRepository.save(feedback3);
            feedBackRepository.save(feedback4);

            final int size = 2;

            // when
            final AdminFeedbackListResponse response = adminFeedbackService.getFeedbackPage(
                    organization.getUuid(), size, null, null, LATEST);

            // then
            assertAll(
                    () -> assertThat(response.feedbacks()).hasSize(size),
                    () -> assertThat(response.hasNext()).isTrue(),
                    () -> assertThat(response.nextCursorId()).isNotNull()
            );
        }

        @Test
        @DisplayName("특정 장소의 피드백이 없을 때 빈 결과를 반환한다")
        void getFeedbackPageByOrganizationId_empty_result() {
            // given
            final Organization organization = OrganizationFixture.createAllBlackBox();
            organizationRepository.save(organization);

            final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                    organization, SUGGESTION);
            organizationCategoryRepository.save(organizationCategory);

            final int size = 10;

            // when
            final AdminFeedbackListResponse response = adminFeedbackService.getFeedbackPage(
                    organization.getUuid(), size, null, null, LATEST);

            // then
            assertAll(
                    () -> assertThat(response.feedbacks()).isEmpty(),
                    () -> assertThat(response.hasNext()).isFalse(),
                    () -> assertThat(response.nextCursorId()).isNull()
            );
        }

        @Test
        @DisplayName("특정 장소의 피드백을 커서 ID와 함께 조회한다")
        void getFeedbackPageByOrganizationId_with_cursor() {
            // given
            final Organization organization = OrganizationFixture.createAllBlackBox();
            organizationRepository.save(organization);

            final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                    organization, SUGGESTION);
            organizationCategoryRepository.save(organizationCategory);

            final Feedback feedback1 = FeedbackFixture.createFeedbackWithOrganization(organization,
                    organizationCategory);
            final Feedback feedback2 = FeedbackFixture.createFeedbackWithOrganization(organization,
                    organizationCategory);
            final Feedback feedback3 = FeedbackFixture.createFeedbackWithOrganization(organization,
                    organizationCategory);
            final Feedback feedback4 = FeedbackFixture.createFeedbackWithOrganization(organization,
                    organizationCategory);

            final Feedback saved1 = feedBackRepository.save(feedback1);
            final Feedback saved2 = feedBackRepository.save(feedback2);
            final Feedback saved3 = feedBackRepository.save(feedback3);
            final Feedback saved4 = feedBackRepository.save(feedback4);

            final int size = 2;
            final Long cursorId = saved3.getId(); // saved3를 커서로 사용하면 saved2, saved1이 반환됨

            // when
            final AdminFeedbackListResponse response = adminFeedbackService.getFeedbackPage(
                    organization.getUuid(), size, cursorId, null, LATEST);

            // then
            assertAll(
                    () -> assertThat(response.feedbacks()).hasSize(2),
                    () -> assertThat(response.feedbacks().get(0).feedbackId()).isEqualTo(saved2.getId()),
                    // DESC 정렬이므로 saved2가 먼저
                    () -> assertThat(response.feedbacks().get(1).feedbackId()).isEqualTo(saved1.getId()),
                    () -> assertThat(response.hasNext()).isFalse()
            );
        }
    }

    @Test
    @DisplayName("피드백의 답글을 추가한다")
    void add_comment() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        final Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
        organizerRepository.save(organizer);

        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback = FeedbackFixture.createFeedbackWithOrganization(organization,
                organizationCategory);
        feedBackRepository.save(feedback);

        String testComment = "testComment";
        UpdateFeedbackCommentRequest updateFeedbackCommentRequest = new UpdateFeedbackCommentRequest(
                testComment
        );

        // when
        final UpdateFeedbackCommentResponse updateFeedbackCommentResponse =
                adminFeedbackService.updateFeedbackComment(
                        admin.getId(),
                        updateFeedbackCommentRequest,
                        feedback.getId()
                );

        // then
        assertThat(updateFeedbackCommentResponse.comment()).isEqualTo(testComment);
    }

    @Test
    @DisplayName("단체에 속하지 않은 관리자가 댓글을 수정하려고 한다면 예외가 발생해야 한다.")
    void not_contains_organization_admin_request_then_throw_exception() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);
        final Admin otherAdmin = AdminFixture.create();

        final Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
        organizerRepository.save(organizer);

        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback = FeedbackFixture.createFeedbackWithOrganization(organization,
                organizationCategory);
        feedBackRepository.save(feedback);

        String testComment = "testComment";
        UpdateFeedbackCommentRequest updateFeedbackCommentRequest = new UpdateFeedbackCommentRequest(
                testComment
        );

        // when & then
        assertThatThrownBy(() -> adminFeedbackService.updateFeedbackComment(otherAdmin.getId(), updateFeedbackCommentRequest, feedback.getId()))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @DisplayName("피드백의 답글을 추가한다면, 상태가 바뀌어야 한다.")
    void when_add_comment_then_change_feedback_status() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        final Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
        organizerRepository.save(organizer);

        final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);

        final Feedback feedback = FeedbackFixture.createFeedbackWithOrganization(organization,
                organizationCategory);
        feedBackRepository.save(feedback);

        String testComment = "testComment";
        UpdateFeedbackCommentRequest updateFeedbackCommentRequest = new UpdateFeedbackCommentRequest(
                testComment
        );

        //when
        adminFeedbackService.updateFeedbackComment(admin.getId(), updateFeedbackCommentRequest, feedback.getId());
        final Feedback resultFeedback = feedBackRepository.findById(1L).get();

        // then
        assertThat(resultFeedback.getStatus()).isEqualTo(ProcessStatus.CONFIRMED);
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

            // 순서대로 저장하여 ID가 증가하도록 함
            final Feedback feedback1 = FeedbackFixture.createFeedbackWithOrganization(organization,
                    organizationCategory);
            final Feedback feedback2 = FeedbackFixture.createFeedbackWithOrganization(organization,
                    organizationCategory);
            final Feedback feedback3 = FeedbackFixture.createFeedbackWithOrganization(organization,
                    organizationCategory);

            final Feedback saved1 = feedBackRepository.save(feedback1);
            final Feedback saved2 = feedBackRepository.save(feedback2);
            final Feedback saved3 = feedBackRepository.save(feedback3);

            // when - LATEST로 정렬
            final AdminFeedbackListResponse response = adminFeedbackService.getFeedbackPage(
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

            // 순서대로 저장하여 ID가 증가하도록 함
            final Feedback feedback1 = FeedbackFixture.createFeedbackWithOrganization(organization,
                    organizationCategory);
            final Feedback feedback2 = FeedbackFixture.createFeedbackWithOrganization(organization,
                    organizationCategory);
            final Feedback feedback3 = FeedbackFixture.createFeedbackWithOrganization(organization,
                    organizationCategory);

            final Feedback saved1 = feedBackRepository.save(feedback1);
            final Feedback saved2 = feedBackRepository.save(feedback2);
            final Feedback saved3 = feedBackRepository.save(feedback3);

            // when - OLDEST로 정렬
            final AdminFeedbackListResponse response = adminFeedbackService.getFeedbackPage(
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

            // 좋아요 수가 다른 피드백들 생성
            final Feedback feedback1 = FeedbackFixture.createFeedbackWithLikes(organization,
                    organizationCategory, 5);
            final Feedback feedback2 = FeedbackFixture.createFeedbackWithLikes(organization,
                    organizationCategory, 10);
            final Feedback feedback3 = FeedbackFixture.createFeedbackWithLikes(organization,
                    organizationCategory, 3);

            final Feedback saved1 = feedBackRepository.save(feedback1);
            final Feedback saved2 = feedBackRepository.save(feedback2);
            final Feedback saved3 = feedBackRepository.save(feedback3);

            // when - LIKES로 정렬
            final AdminFeedbackListResponse response = adminFeedbackService.getFeedbackPage(
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
}
