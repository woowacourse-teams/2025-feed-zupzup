package feedzupzup.backend.organization.domain;

import static feedzupzup.backend.feedback.domain.vo.ProcessStatus.CONFIRMED;
import static feedzupzup.backend.feedback.domain.vo.ProcessStatus.WAITING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.admin.domain.fixture.AdminFixture;
import feedzupzup.backend.category.domain.Category;
import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.OrganizationCategoryRepository;
import feedzupzup.backend.config.RepositoryHelper;
import feedzupzup.backend.feedback.domain.Feedback;
import feedzupzup.backend.feedback.domain.FeedbackRepository;
import feedzupzup.backend.feedback.fixture.FeedbackFixture;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import feedzupzup.backend.organizer.domain.Organizer;
import feedzupzup.backend.organizer.domain.OrganizerRepository;
import feedzupzup.backend.organizer.domain.OrganizerRole;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class OrganizationRepositoryTest extends RepositoryHelper {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizerRepository organizerRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private OrganizationCategoryRepository organizationCategoryRepository;

    @Nested
    @DisplayName("관리자 소속 단체 정보 조회 시")
    class GetAdminOrganizationInfos {

        private Admin admin;
        private Organization organization1;
        private Organization organization2;
        private OrganizationCategory organizationCategory1;
        private OrganizationCategory organizationCategory2;

        @BeforeEach
        void setUp() {
            admin = adminRepository.save(AdminFixture.create());
            organization1 = organizationRepository.save(OrganizationFixture.createAllBlackBox());
            organization2 = organizationRepository.save(OrganizationFixture.createAllBlackBox());

            organizerRepository.save(new Organizer(organization1, admin, OrganizerRole.OWNER));
            organizerRepository.save(new Organizer(organization2, admin, OrganizerRole.OWNER));

            organizationCategory1 = organizationCategoryRepository.save(
                    new OrganizationCategory(organization1, Category.ETC));
            organizationCategory2 = organizationCategoryRepository.save(
                    new OrganizationCategory(organization2, Category.ETC));
        }

        @Test
        @DisplayName("피드백이 없는 경우 단체 정보만 조회한다.")
        void getAdminOrganizationInfos_NoFeedback() {
            // when
            List<AdminOrganizationInfo> adminOrganizationInfos = organizationRepository.getAdminOrganizationInfos(
                    admin.getId());

            // then
            assertThat(adminOrganizationInfos).hasSize(2);
        }

        @Test
        @DisplayName("피드백이 있는 경우 피드백 정보와 함께 단체 정보를 조회한다.")
        void getAdminOrganizationInfos_WithFeedback() {
            // given

            final Feedback feedback1 = FeedbackFixture.createFeedbackWithStatus(organization1,
                    WAITING,
                    organizationCategory1);
            feedbackRepository.save(feedback1);

            final Feedback feedback2 = FeedbackFixture.createFeedbackWithStatus(organization2,
                    WAITING,
                    organizationCategory2);
            feedbackRepository.save(feedback2);

            AdminOrganizationInfo expected1 = new AdminOrganizationInfo(
                    organization1.getUuid(),
                    organization1.getName().getValue(),
                    1L,
                    feedback1.getPostedAt().getValue()
            );

            AdminOrganizationInfo expected2 = new AdminOrganizationInfo(
                    organization2.getUuid(),
                    organization2.getName().getValue(),
                    1L,
                    feedback2.getPostedAt().getValue()
            );

            // when
            List<AdminOrganizationInfo> adminOrganizationInfos = organizationRepository.getAdminOrganizationInfos(
                    admin.getId());

            // then
            assertAll(
                    () -> assertThat(adminOrganizationInfos).hasSize(2),
                    () -> assertThat(adminOrganizationInfos).contains(expected1),
                    () -> assertThat(adminOrganizationInfos).contains(expected2)
            );
        }

        @Test
        @DisplayName("하나의 단체에 여러 피드백이 있는 경우에도 정확한 피드백 개수를 조회한다.")
        void getAdminOrganizationInfos_WithMultipleFeedbacks() {
            // given
            feedbackRepository.save(FeedbackFixture.createFeedbackWithStatus(organization1, WAITING,
                    organizationCategory1));
            feedbackRepository.save(FeedbackFixture.createFeedbackWithStatus(organization1, WAITING,
                    organizationCategory1));
            feedbackRepository.save(
                    FeedbackFixture.createFeedbackWithStatus(organization1, CONFIRMED,
                            organizationCategory1));

            // when
            List<AdminOrganizationInfo> adminOrganizationInfos = organizationRepository.getAdminOrganizationInfos(
                    admin.getId());

            // then
            AdminOrganizationInfo organizationInfo = adminOrganizationInfos.stream()
                    .filter(info -> info.organizationName()
                            .equals(organization1.getName().getValue()))
                    .findFirst()
                    .get();

            assertThat(organizationInfo.waitingCount()).isEqualTo(2L);
        }
    }
}
