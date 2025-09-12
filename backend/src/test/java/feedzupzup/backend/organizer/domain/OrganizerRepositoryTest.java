package feedzupzup.backend.organizer.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.admin.domain.fixture.AdminFixture;
import feedzupzup.backend.config.RepositoryHelper;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class OrganizerRepositoryTest extends RepositoryHelper {

    @Autowired
    private OrganizerRepository organizerRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Test
    @DisplayName("관리자 ID로 조직과 함께 페치된 오거나이저 목록을 조회한다")
    void findAllFetchedByAdminId_success() {
        // given
        Admin admin = AdminFixture.create();
        adminRepository.save(admin);

        Organization organization1 = OrganizationFixture.createAllBlackBox();
        Organization organization2 = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization1);
        organizationRepository.save(organization2);

        Organizer organizer1 = new Organizer(organization1, admin, OrganizerRole.OWNER);
        Organizer organizer2 = new Organizer(organization2, admin, OrganizerRole.OWNER);
        organizerRepository.save(organizer1);
        organizerRepository.save(organizer2);

        // when
        List<Organizer> organizers = organizerRepository.findAllFetchedByAdminId(admin.getId());

        // then
        assertAll(
                () -> assertThat(organizers).hasSize(2),
                () -> assertThat(organizers.get(0).getOrganization()).isNotNull(),
                () -> assertThat(organizers.get(1).getOrganization()).isNotNull()
        );
    }

    @Test
    @DisplayName("존재하지 않는 관리자 ID로 조회하면 빈 목록을 반환한다")
    void findAllFetchedByAdminId_notFound() {
        // when
        List<Organizer> organizers = organizerRepository.findAllFetchedByAdminId(999L);

        // then
        assertThat(organizers).isEmpty();
    }
}