package feedzupzup.backend.organization.application;

import static org.assertj.core.api.Assertions.assertThat;

import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.dto.request.CheeringRequest;
import feedzupzup.backend.organization.dto.response.CheeringResponse;
import feedzupzup.backend.organization.dto.response.UserOrganizationResponse;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserOrganizationServiceTest extends ServiceIntegrationHelper {

    @Autowired
    private UserOrganizationService userOrganizationService;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Test
    @DisplayName("단체 ID로 단체를 성공적으로 조회한다")
    void get_organization_by_id_success() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();

        final Organization savedOrganization = organizationRepository.save(organization);

        // when
        final UserOrganizationResponse response = userOrganizationService.getOrganizationByUuid(
                savedOrganization.getUuid());

        // then
        assertThat(response.organizationName()).isEqualTo(organization.getName().getValue());
        assertThat(response.totalCheeringCount()).isEqualTo(organization.getCheeringCountValue());
    }

    @Test
    @DisplayName("요청한 응원 수만큼 단체의 응원 총 횟수가 증가한다.")
    void cheer_increases_total_count() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();

        final Organization savedOrganization = organizationRepository.save(organization);
        final CheeringRequest request = new CheeringRequest(100);

        // when
        final CheeringResponse response = userOrganizationService.cheer(
                request, savedOrganization.getUuid());

        // then
        assertThat(response.cheeringTotalCount()).isEqualTo(100);
    }
}
