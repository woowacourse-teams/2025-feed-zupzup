package feedzupzup.backend.feedback.application;

import static feedzupzup.backend.category.domain.Category.SUGGESTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.admin.domain.fixture.AdminFixture;
import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.OrganizationCategoryRepository;
import feedzupzup.backend.category.fixture.OrganizationCategoryFixture;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.domain.vo.ProcessStatus;
import feedzupzup.backend.feedback.dto.request.CreateFeedbackRequest;
import feedzupzup.backend.feedback.dto.request.UpdateFeedbackCommentRequest;
import feedzupzup.backend.feedback.dto.response.CreateFeedbackResponse;
import feedzupzup.backend.global.util.CurrentDateTime;
import feedzupzup.backend.guest.domain.guest.Guest;
import feedzupzup.backend.guest.domain.guest.GuestRepository;
import feedzupzup.backend.guest.dto.GuestInfo;
import feedzupzup.backend.organization.application.OrganizationStatisticService;
import feedzupzup.backend.organization.domain.OrganizationStatistic;
import feedzupzup.backend.organization.domain.OrganizationStatisticRepository;
import feedzupzup.backend.organization.dto.response.OrganizationStatisticResponse;
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

public class OrganizationStatisticServiceTest extends ServiceIntegrationHelper {

    @Autowired
    private FeedbackRepository feedBackRepository;

    @Autowired
    private UserFeedbackService userFeedbackService;

    @Autowired
    private AdminFeedbackService adminFeedbackService;

    @Autowired
    private GuestRepository guestRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizerRepository organizerRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private OrganizationStatisticRepository organizationStatisticRepository;

    @Autowired
    private OrganizationStatisticService organizationStatisticService;

    @Autowired
    private OrganizationCategoryRepository organizationCategoryRepository;

    Admin admin;
    Guest guest;
    Organization organization;
    Organizer organizer;
    OrganizationStatistic organizationStatistic;
    OrganizationCategory organizationCategory;

    @BeforeEach
    void init() {
        admin = AdminFixture.create();
        adminRepository.save(admin);

        guest = new Guest(UUID.randomUUID(), CurrentDateTime.create());
        guestRepository.save(guest);

        organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        organizer = new Organizer(organization, admin, OrganizerRole.OWNER);
        organizerRepository.save(organizer);

        organizationStatistic = new OrganizationStatistic(organization);
        organizationStatisticRepository.save(organizationStatistic);

        organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                organization, SUGGESTION);
        organizationCategoryRepository.save(organizationCategory);
    }

    @Test
    @DisplayName("피드백 상태에 따른 통계를 계산한다.")
    void calculateStatistic_success() {
        // given

        // 피드백 저장
        final CreateFeedbackResponse createFeedbackResponse1 = userFeedbackService.create(
                createFeedbackRequest(), organization.getUuid(), toGuestInfo(guest));

        final CreateFeedbackResponse createFeedbackResponse2 = userFeedbackService.create(
                createFeedbackRequest(), organization.getUuid(), toGuestInfo(guest));

        userFeedbackService.create(
                createFeedbackRequest(), organization.getUuid(), toGuestInfo(guest));

        adminFeedbackService.updateFeedbackComment(admin.getId(), new UpdateFeedbackCommentRequest(""), createFeedbackResponse1.feedbackId());
        adminFeedbackService.updateFeedbackComment(admin.getId(), new UpdateFeedbackCommentRequest(""), createFeedbackResponse2.feedbackId());


        // when
        final OrganizationStatisticResponse response = organizationStatisticService.calculateStatistic(
                organization.getUuid());

        // then
        assertAll(
                () -> assertThat(response.totalCount()).isEqualTo(3), // 모든 피드백 포함
                () -> assertThat(response.confirmedCount()).isEqualTo(2), // CONFIRMED 상태 피드백 2개
                () -> assertThat(response.waitingCount()).isEqualTo(1), // WAITING 상태 피드백 1개
                () -> assertThat(response.reflectionRate()).isEqualTo(67)
        );
    }

    @Test
    @DisplayName("통계 메서드 호출 중 존재하지 않는 place가 주어진다면, 예외가 발생해야 한다.")
    void calculateStatistic_withNoFeedbacks() {
        // given
        final UUID organizationUuid = UUID.randomUUID(); // 존재하지 않는 장소 ID

        // when & then
        assertThatThrownBy(() -> organizationStatisticService.calculateStatistic(organizationUuid))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Nested
    @DisplayName("ProcessStatus와 organizationId 따라, 통계를 계산할 수 있어야 한다.")
    class CalculateStatisticWithVariousStatuses {

        @Test
        @DisplayName("본인 organizationId에 속한 피드백만 계산을 해야한다.")
        void only_calculate_own_organization_feedback() {
            // given
            final Organization otherOrganization = OrganizationFixture.createAllBlackBox();
            organizationRepository.save(otherOrganization);

            final OrganizationStatistic otherOrganizationStatistic = new OrganizationStatistic(
                    otherOrganization);
            organizationStatisticRepository.save(otherOrganizationStatistic);

            final OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                    organization, SUGGESTION);
            organizationCategoryRepository.save(organizationCategory);

            final Feedback feedback1 = FeedbackFixture.createFeedbackWithStatus(
                    organization, ProcessStatus.WAITING, organizationCategory);
            final Feedback feedback2 = FeedbackFixture.createFeedbackWithStatus(
                    organization, ProcessStatus.WAITING, organizationCategory);

            feedBackRepository.save(feedback1);
            feedBackRepository.save(feedback2);

            // when
            final OrganizationStatisticResponse response = organizationStatisticService.calculateStatistic(
                    otherOrganization.getUuid());

            // then
            assertAll(
                    () -> assertThat(response.totalCount()).isEqualTo(0),
                    () -> assertThat(response.waitingCount()).isEqualTo(0),
                    () -> assertThat(response.reflectionRate()).isEqualTo(0),
                    () -> assertThat(response.confirmedCount()).isEqualTo(0)
            );
        }

        @Test
        @DisplayName("확인 3개, 대기 2개일 경우, 총 5개의 피드백, 60개의 처리율이 구해져야 한다.")
        void one_day_boundary_value() {
            // given
            final CreateFeedbackResponse createFeedbackResponse1 = userFeedbackService.create(
                    createFeedbackRequest(), organization.getUuid(), toGuestInfo(guest));
            final CreateFeedbackResponse createFeedbackResponse2 = userFeedbackService.create(
                    createFeedbackRequest(), organization.getUuid(), toGuestInfo(guest));
            final CreateFeedbackResponse createFeedbackResponse3 = userFeedbackService.create(
                    createFeedbackRequest(), organization.getUuid(), toGuestInfo(guest));

            userFeedbackService.create(createFeedbackRequest(), organization.getUuid(), toGuestInfo(guest));
            userFeedbackService.create(createFeedbackRequest(), organization.getUuid(), toGuestInfo(guest));

            adminFeedbackService.updateFeedbackComment(admin.getId(), new UpdateFeedbackCommentRequest(""), createFeedbackResponse1.feedbackId());
            adminFeedbackService.updateFeedbackComment(admin.getId(), new UpdateFeedbackCommentRequest(""), createFeedbackResponse2.feedbackId());
            adminFeedbackService.updateFeedbackComment(admin.getId(), new UpdateFeedbackCommentRequest(""), createFeedbackResponse3.feedbackId());

            // when
            final OrganizationStatisticResponse response = organizationStatisticService.calculateStatistic(
                    organization.getUuid());

            // then
            assertAll(
                    () -> assertThat(response.totalCount()).isEqualTo(5),
                    () -> assertThat(response.waitingCount()).isEqualTo(2),
                    () -> assertThat(response.confirmedCount()).isEqualTo(3),
                    () -> assertThat(response.reflectionRate()).isEqualTo(60)
            );
        }
    }
    
    private CreateFeedbackRequest createFeedbackRequest() {
        return new CreateFeedbackRequest(
                "test1", false, "testUser", "건의", null
        );
    }

    private GuestInfo toGuestInfo(Guest guest) {
        return new GuestInfo(guest.getGuestUuid());
    }
}
