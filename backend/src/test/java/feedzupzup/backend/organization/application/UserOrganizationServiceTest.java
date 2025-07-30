package feedzupzup.backend.organization.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.dto.request.CheeringRequest;
import feedzupzup.backend.organization.dto.response.CheeringResponse;
import feedzupzup.backend.organization.dto.response.UserOrganizationResponse;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserOrganizationServiceTest extends ServiceIntegrationHelper {

    @Autowired
    private UserOrganizationService userOrganizationService;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Nested
    @DisplayName("단체 조회 테스트")
    class GetOrganizationByIdTest {

        @Test
        @DisplayName("단체 ID로 단체를 성공적으로 조회한다")
        void get_organization_by_id_success() {
            // given
            final Organization organization = OrganizationFixture.createAllBlackBox();

            final Organization savedOrganization = organizationRepository.save(organization);

            // when
            final UserOrganizationResponse response = userOrganizationService.getOrganizationById(
                    savedOrganization.getId());

            // then
            assertThat(response.organizationName()).isEqualTo(organization.getName());
        }

        @Test
        @DisplayName("존재하지 않는 단체 ID로 조회 시 예외를 발생시킨다")
        void get_organization_by_id_not_found() {
            // given
            final Long nonExistentOrganizationId = 999L;

            // when & then
            assertThatThrownBy(() -> userOrganizationService.getOrganizationById(nonExistentOrganizationId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("해당 ID(id = " + nonExistentOrganizationId + ")인 단체를 찾을 수 없습니다.");
        }
    }

    @Test
    @DisplayName("요청한 응원 수만큼 단체의 응원 총 횟수가 증가한다.")
    void get_organization_by_id_success() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();

        final Organization savedOrganization = organizationRepository.save(organization);
        CheeringRequest request = new CheeringRequest(100);

        // when
        final CheeringResponse response = userOrganizationService.cheer(request, savedOrganization.getId());

        // then
        assertThat(response.cheeringTotalCount()).isEqualTo(100);
    }
}
