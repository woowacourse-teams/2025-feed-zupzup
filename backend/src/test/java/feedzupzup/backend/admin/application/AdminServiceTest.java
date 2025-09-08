package feedzupzup.backend.admin.application;

import static org.assertj.core.api.Assertions.assertThat;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.admin.domain.fixture.AdminFixture;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.notification.domain.NotificationToken;
import feedzupzup.backend.notification.domain.NotificationTokenRepository;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import feedzupzup.backend.organizer.domain.Organizer;
import feedzupzup.backend.organizer.domain.OrganizerRepository;
import feedzupzup.backend.organizer.domain.OrganizerRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AdminServiceTest extends ServiceIntegrationHelper {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private NotificationTokenRepository notificationTokenRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizerRepository organizerRepository;

    @Test
    @DisplayName("관리자 회원탈퇴시 관련 모든 데이터가 삭제된다")
    void withdraw_success() {
        // given
        Admin admin = AdminFixture.create();
        adminRepository.save(admin);

        NotificationToken notificationToken = new NotificationToken(admin, "test-token");
        notificationTokenRepository.save(notificationToken);

        Organization organization = OrganizationFixture.createAllBlackBox();
        Organization savedOrganization = organizationRepository.save(organization);

        Organizer organizer = new Organizer(savedOrganization, admin, OrganizerRole.OWNER);
        organizerRepository.save(organizer);

        // when
        adminService.withdraw(admin.getId());

        // then
        assertThat(adminRepository.findById(admin.getId())).isEmpty();
        assertThat(notificationTokenRepository.findByAdminIdAndValue(admin.getId(), "test-token")).isEmpty();
        assertThat(organizerRepository.findAllFetchedByAdminId(admin.getId())).isEmpty();
    }
}
