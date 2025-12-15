package feedzupzup.backend.organizer.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.admin.domain.fixture.AdminFixture;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.organization.application.AdminOrganizationService;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import feedzupzup.backend.organizer.domain.Organizer;
import feedzupzup.backend.organizer.domain.OrganizerRepository;
import feedzupzup.backend.organizer.domain.OrganizerRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class OrganizerServiceTest extends ServiceIntegrationHelper {

    @Autowired
    private OrganizerService organizerService;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrganizerRepository organizerRepository;

    @Autowired
    private AdminOrganizationService adminOrganizationService;

    @Test
    @DisplayName("관리자 ID로 오거나이저들을 삭제하면서 연관된 조직 데이터도 정리한다")
    void deleteAllByAdminId_success() {
        // given
        Admin admin = AdminFixture.create();
        adminRepository.save(admin);

        Organization organization1 = OrganizationFixture.createAllBlackBox();
        Organization organization2 = OrganizationFixture.createAllBlackBox();
        Organization save1 = organizationRepository.save(organization1);
        Organization save2 = organizationRepository.save(organization2);

        Organizer organizer1 = new Organizer(save1, admin, OrganizerRole.OWNER);
        Organizer organizer2 = new Organizer(save2, admin, OrganizerRole.OWNER);
        organizerRepository.save(organizer1);
        organizerRepository.save(organizer2);

        // when
        organizerService.deleteAllByAdminId(admin.getId());

        // then
        assertAll(
                () -> assertThat(organizerRepository.findAllFetchedByAdminId(admin.getId())).isEmpty()
        );
    }

    @Test
    @DisplayName("관리자에게 연관된 오거나이저가 없는 경우 정상적으로 처리한다")
    void deleteAllByAdminId_noOrganizers() {
        // given
        Admin admin = AdminFixture.create();
        adminRepository.save(admin);

        // when
        organizerService.deleteAllByAdminId(admin.getId());

        // then
        assertAll(
                () -> assertThat(organizerRepository.findAllFetchedByAdminId(admin.getId())).isEmpty()
        );
    }
}
