package feedzupzup.backend.feedback.application;

import static feedzupzup.backend.category.domain.Category.SUGGESTION;
import static feedzupzup.backend.feedback.domain.vo.FeedbackSortBy.LATEST;
import static feedzupzup.backend.feedback.domain.vo.FeedbackSortBy.LIKES;
import static feedzupzup.backend.feedback.domain.vo.FeedbackSortBy.OLDEST;
import static feedzupzup.backend.feedback.dto.response.ClusterRepresentativeFeedbacksResponse.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.admin.domain.fixture.AdminFixture;
import feedzupzup.backend.auth.exception.AuthException.ForbiddenException;
import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.OrganizationCategoryRepository;
import feedzupzup.backend.category.fixture.OrganizationCategoryFixture;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.domain.vo.ProcessStatus;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackCommentRequest;
import feedzupzup.backend.feedback.dto.response.AdminFeedbackListResponse;
import feedzupzup.backend.feedback.dto.response.ClusterFeedbacksResponse;
import feedzupzup.backend.feedback.dto.response.ClusterRepresentativeFeedbacksResponse;
import feedzupzup.backend.feedback.dto.response.FeedbackItem;
import feedzupzup.backend.feedback.dto.response.FeedbackStatisticResponse;
import feedzupzup.backend.feedback.dto.response.UpdateFeedbackCommentResponse;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import feedzupzup.backend.organizer.domain.Organizer;
import feedzupzup.backend.organizer.domain.OrganizerRepository;
import feedzupzup.backend.organizer.domain.OrganizerRole;
import java.util.UUID;
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

            final Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
            organizerRepository.save(organizer);

            final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                    organization, SUGGESTION);
            organizationCategoryRepository.save(organizationCategory);

            final Feedback feedback = FeedbackFixture.createFeedbackWithOrganization(organization,
                    organizationCategory);
            final Feedback savedFeedback = feedBackRepository.save(feedback);

            // when
            adminFeedbackService.delete(admin.getId(), savedFeedback.getId());

            // then
            assertThat(feedBackRepository.findById(savedFeedback.getId())).isEmpty();
        }

        @Test
        @DisplayName("관리자가 속한 단체가 아닐경우, 삭제 시 예외가 발생해야 한다")
        void not_contains_organization_delete_api_then_throw_exception() {
            final Organization organization = OrganizationFixture.createAllBlackBox();
            organizationRepository.save(organization);

            final Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
            organizerRepository.save(organizer);

            final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                    organization, SUGGESTION);
            organizationCategoryRepository.save(organizationCategory);

            final Feedback feedback = FeedbackFixture.createFeedbackWithOrganization(organization,
                    organizationCategory);
            final Feedback savedFeedback = feedBackRepository.save(feedback);

            final Admin otherAdmin = AdminFixture.createFromLoginId("admin999");
            adminRepository.save(otherAdmin);

            assertThatThrownBy(() -> adminFeedbackService.delete(otherAdmin.getId(), savedFeedback.getId()))
                    .isInstanceOf(ForbiddenException.class);
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
        @DisplayName("존재하지 않는 단체를 조회하면 예외를 발생시킨다.")
        void getAllFeedbacks_empty_result() {
            // given
            final UUID organizationUuid = UUID.randomUUID();
            final int size = 10;

            // when
            assertThatThrownBy(() -> {
                adminFeedbackService.getFeedbackPage(
                        organizationUuid, size, null, null, LATEST);
            }).isInstanceOf(ResourceNotFoundException.class);
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
        assertThatThrownBy(
                () -> adminFeedbackService.updateFeedbackComment(otherAdmin.getId(), updateFeedbackCommentRequest,
                        feedback.getId()))
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

    @Nested
    @DisplayName("피드백 통계 계산 테스트")
    class CalculateFeedbackStatisticsTest {

        @Test
        @DisplayName("관리자가 관리하는 모든 조직의 피드백 통계를 성공적으로 계산한다")
        void calculateFeedbackStatistics_success() {
            // given
            final Organization organization1 = OrganizationFixture.createAllBlackBox();
            final Organization organization2 = OrganizationFixture.createAllBlackBox();
            organizationRepository.save(organization1);
            organizationRepository.save(organization2);

            // 관리자를 두 조직에 등록
            final Organizer organizer1 = new Organizer(organization1, admin, OrganizerRole.OWNER);
            final Organizer organizer2 = new Organizer(organization2, admin, OrganizerRole.OWNER);
            organizerRepository.save(organizer1);
            organizerRepository.save(organizer2);

            final OrganizationCategory orgCategory1 = OrganizationCategoryFixture.createOrganizationCategory(
                    organization1, SUGGESTION);
            final OrganizationCategory orgCategory2 = OrganizationCategoryFixture.createOrganizationCategory(
                    organization2, SUGGESTION);
            organizationCategoryRepository.save(orgCategory1);
            organizationCategoryRepository.save(orgCategory2);

            // organization1에 피드백 3개 (완료 2개, 대기 1개)
            final Feedback confirmedFeedback1 = FeedbackFixture.createFeedbackWithStatus(organization1,
                    ProcessStatus.CONFIRMED, orgCategory1);
            final Feedback confirmedFeedback2 = FeedbackFixture.createFeedbackWithStatus(organization1,
                    ProcessStatus.CONFIRMED, orgCategory1);
            final Feedback waitingFeedback1 = FeedbackFixture.createFeedbackWithStatus(organization1,
                    ProcessStatus.WAITING, orgCategory1);

            // organization2에 피드백 2개 (완료 1개, 대기 1개)
            final Feedback confirmedFeedback3 = FeedbackFixture.createFeedbackWithStatus(organization2,
                    ProcessStatus.CONFIRMED, orgCategory2);
            final Feedback waitingFeedback2 = FeedbackFixture.createFeedbackWithStatus(organization2,
                    ProcessStatus.WAITING, orgCategory2);

            feedBackRepository.save(confirmedFeedback1);
            feedBackRepository.save(confirmedFeedback2);
            feedBackRepository.save(waitingFeedback1);
            feedBackRepository.save(confirmedFeedback3);
            feedBackRepository.save(waitingFeedback2);

            // when
            final FeedbackStatisticResponse response = adminFeedbackService.calculateFeedbackStatistics(admin.getId());

            // then - 총 5개 피드백 중 3개 완료 (60%)
            assertAll(
                    () -> assertThat(response.totalCount()).isEqualTo(5),
                    () -> assertThat(response.confirmedCount()).isEqualTo(3),
                    () -> assertThat(response.reflectionRate()).isEqualTo(60)
            );
        }

        @Test
        @DisplayName("피드백이 없는 경우 모든 값이 0으로 반환된다")
        void calculateFeedbackStatistics_no_feedbacks() {
            // given
            final Organization organization = OrganizationFixture.createAllBlackBox();
            organizationRepository.save(organization);

            final Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
            organizerRepository.save(organizer);

            // when
            final FeedbackStatisticResponse response = adminFeedbackService.calculateFeedbackStatistics(admin.getId());

            // then
            assertAll(
                    () -> assertThat(response.totalCount()).isEqualTo(0),
                    () -> assertThat(response.confirmedCount()).isEqualTo(0),
                    () -> assertThat(response.reflectionRate()).isEqualTo(0)
            );
        }

        @Test
        @DisplayName("삭제된 피드백은 통계에 포함되지 않는다")
        void calculateFeedbackStatistics_excludes_deleted_feedbacks() {
            // given
            final Organization organization = OrganizationFixture.createAllBlackBox();
            organizationRepository.save(organization);

            final Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
            organizerRepository.save(organizer);

            final OrganizationCategory orgCategory = OrganizationCategoryFixture.createOrganizationCategory(
                    organization, SUGGESTION);
            organizationCategoryRepository.save(orgCategory);

            // 일반 피드백 2개 (완료 1개, 대기 1개)
            final Feedback confirmedFeedback = FeedbackFixture.createFeedbackWithStatus(organization,
                    ProcessStatus.CONFIRMED, orgCategory);
            final Feedback waitingFeedback = FeedbackFixture.createFeedbackWithStatus(organization,
                    ProcessStatus.WAITING, orgCategory);

            feedBackRepository.save(confirmedFeedback);
            feedBackRepository.save(waitingFeedback);

            // 피드백 1개 삭제
            feedBackRepository.deleteById(waitingFeedback.getId());

            // when
            final FeedbackStatisticResponse response = adminFeedbackService.calculateFeedbackStatistics(admin.getId());

            // then - 삭제된 피드백은 제외되어 총 1개 (완료 1개, 100%)
            assertAll(
                    () -> assertThat(response.totalCount()).isEqualTo(1),
                    () -> assertThat(response.confirmedCount()).isEqualTo(1),
                    () -> assertThat(response.reflectionRate()).isEqualTo(100)
            );
        }

        @Test
        @DisplayName("다른 관리자의 조직 피드백은 통계에 포함되지 않는다")
        void calculateFeedbackStatistics_excludes_other_admin_feedbacks() {
            // given
            final Admin otherAdmin = AdminFixture.createFromLoginId("otheradmin");
            adminRepository.save(otherAdmin);

            final Organization myOrganization = OrganizationFixture.createAllBlackBox();
            final Organization otherOrganization = OrganizationFixture.createAllBlackBox();
            organizationRepository.save(myOrganization);
            organizationRepository.save(otherOrganization);

            // 현재 관리자가 관리하는 조직
            final Organizer myOrganizer = new Organizer(myOrganization, admin, OrganizerRole.OWNER);
            // 다른 관리자가 관리하는 조직
            final Organizer otherOrganizer = new Organizer(otherOrganization, otherAdmin, OrganizerRole.OWNER);
            organizerRepository.save(myOrganizer);
            organizerRepository.save(otherOrganizer);

            final OrganizationCategory myOrgCategory = OrganizationCategoryFixture.createOrganizationCategory(
                    myOrganization, SUGGESTION);
            final OrganizationCategory otherOrgCategory = OrganizationCategoryFixture.createOrganizationCategory(
                    otherOrganization, SUGGESTION);
            organizationCategoryRepository.save(myOrgCategory);
            organizationCategoryRepository.save(otherOrgCategory);

            // 내 조직에 피드백 1개
            final Feedback myFeedback = FeedbackFixture.createFeedbackWithStatus(
                    myOrganization, ProcessStatus.CONFIRMED, myOrgCategory);
            // 다른 관리자 조직에 피드백 2개
            final Feedback otherFeedback1 = FeedbackFixture.createFeedbackWithStatus(
                    otherOrganization, ProcessStatus.CONFIRMED, otherOrgCategory);
            final Feedback otherFeedback2 = FeedbackFixture.createFeedbackWithStatus(
                    otherOrganization, ProcessStatus.WAITING, otherOrgCategory);

            feedBackRepository.save(myFeedback);
            feedBackRepository.save(otherFeedback1);
            feedBackRepository.save(otherFeedback2);

            // when
            final FeedbackStatisticResponse response = adminFeedbackService.calculateFeedbackStatistics(admin.getId());

            // then - 내 조직의 피드백만 포함 (총 1개, 완료 1개, 100%)
            assertAll(
                    () -> assertThat(response.totalCount()).isEqualTo(1),
                    () -> assertThat(response.confirmedCount()).isEqualTo(1),
                    () -> assertThat(response.reflectionRate()).isEqualTo(100)
            );
        }

        @Test
        @DisplayName("반영률이 올바르게 반올림된다")
        void calculateFeedbackStatistics_reflection_rate_rounding() {
            // given
            final Organization organization = OrganizationFixture.createAllBlackBox();
            organizationRepository.save(organization);

            final Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
            organizerRepository.save(organizer);

            final OrganizationCategory orgCategory = OrganizationCategoryFixture.createOrganizationCategory(
                    organization, SUGGESTION);
            organizationCategoryRepository.save(orgCategory);

            // 총 3개 피드백 중 1개 완료 (33.33...% → 33%)
            final Feedback confirmedFeedback = FeedbackFixture.createFeedbackWithStatus(
                    organization, ProcessStatus.CONFIRMED, orgCategory);
            final Feedback waitingFeedback1 = FeedbackFixture.createFeedbackWithStatus(
                    organization, ProcessStatus.WAITING, orgCategory);
            final Feedback waitingFeedback2 = FeedbackFixture.createFeedbackWithStatus(
                    organization, ProcessStatus.WAITING, orgCategory);

            feedBackRepository.save(confirmedFeedback);
            feedBackRepository.save(waitingFeedback1);
            feedBackRepository.save(waitingFeedback2);

            // when
            final FeedbackStatisticResponse response = adminFeedbackService.calculateFeedbackStatistics(admin.getId());

            // then - 1/3 = 33.33...% → 33%로 반올림
            assertAll(
                    () -> assertThat(response.totalCount()).isEqualTo(3),
                    () -> assertThat(response.confirmedCount()).isEqualTo(1),
                    () -> assertThat(response.reflectionRate()).isEqualTo(33)
            );
        }
    }

    @Nested
    @DisplayName("클러스터 대표 피드백 조회 테스트")
    class GetRepresentativeClusterTest {

        @Test
        @DisplayName("관리자가 속한 조직의 클러스터 대표 피드백을 성공적으로 조회한다")
        void getRepresentativeCluster_success() {
            // given
            final Organization organization = OrganizationFixture.createAllBlackBox();
            organizationRepository.save(organization);
            final Organizer organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
            organizerRepository.save(organizer);
            final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                    organization, SUGGESTION);
            organizationCategoryRepository.save(organizationCategory);

            // 클러스터별 피드백 생성
            final UUID clusterId1 = UUID.randomUUID();
            final UUID clusterId2 = UUID.randomUUID();

            final Feedback feedback1 = FeedbackFixture.createFeedbackWithCluster(
                    organization, "첫 번째 클러스터 대표", organizationCategory, clusterId1);
            final Feedback feedback2 = FeedbackFixture.createFeedbackWithCluster(
                    organization, "첫 번째 클러스터 추가", organizationCategory, clusterId1);
            final Feedback feedback3 = FeedbackFixture.createFeedbackWithCluster(
                    organization, "두 번째 클러스터 대표", organizationCategory, clusterId2);
            
            feedBackRepository.save(feedback1);
            feedBackRepository.save(feedback2);
            feedBackRepository.save(feedback3);

            // when
            final ClusterRepresentativeFeedbacksResponse response = adminFeedbackService.getRepresentativeCluster(
                    admin.getId(), organization.getUuid());

            // then
            assertAll(
                    () -> assertThat(response.clusterRepresentativeFeedbacks()).hasSize(2),
                    () -> assertThat(response.clusterRepresentativeFeedbacks())
                            .extracting(ClusterRepresentativeFeedbackResponse::clusterId)
                            .containsExactlyInAnyOrder(clusterId1, clusterId2),
                    () -> assertThat(response.clusterRepresentativeFeedbacks())
                            .anyMatch(cluster -> cluster.clusterId().equals(clusterId1) && cluster.totalCount() == 2),
                    () -> assertThat(response.clusterRepresentativeFeedbacks())
                            .anyMatch(cluster -> cluster.clusterId().equals(clusterId2) && cluster.totalCount() == 1)
            );
        }
    }

    @Nested
    @DisplayName("특정 클러스터 피드백 조회 테스트")
    class GetFeedbacksByClusterIdTest {

        @Test
        @DisplayName("특정 클러스터의 모든 피드백을 성공적으로 조회한다")
        void getFeedbacksByClusterId_success() {
            // given
            final Organization organization = OrganizationFixture.createAllBlackBox();
            organizationRepository.save(organization);
            final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                    organization, SUGGESTION);
            organizationCategoryRepository.save(organizationCategory);

            final UUID targetClusterId = UUID.randomUUID();
            final UUID otherClusterId = UUID.randomUUID();

            // 같은 클러스터의 피드백 3개
            final Feedback feedback1 = FeedbackFixture.createFeedbackWithCluster(
                    organization, "첫 번째 피드백", organizationCategory, targetClusterId);
            final Feedback feedback2 = FeedbackFixture.createFeedbackWithCluster(
                    organization, "두 번째 피드백", organizationCategory, targetClusterId);
            final Feedback feedback3 = FeedbackFixture.createFeedbackWithCluster(
                    organization, "세 번째 피드백", organizationCategory, targetClusterId);

            // 다른 클러스터의 피드백 1개 (결과에 포함되지 않아야 함)
            final Feedback otherFeedback = FeedbackFixture.createFeedbackWithCluster(
                    organization, "다른 클러스터 피드백", organizationCategory, otherClusterId);

            feedBackRepository.save(feedback1);
            feedBackRepository.save(feedback2);
            feedBackRepository.save(feedback3);
            feedBackRepository.save(otherFeedback);

            // when
            final ClusterFeedbacksResponse response = adminFeedbackService.getFeedbacksByClusterId(targetClusterId);

            // then
            assertAll(
                    () -> assertThat(response.feedbacks()).hasSize(3),
                    () -> assertThat(response.feedbacks())
                            .extracting(FeedbackItem::content)
                            .containsExactlyInAnyOrder("첫 번째 피드백", "두 번째 피드백", "세 번째 피드백")
            );
        }
    }
}
