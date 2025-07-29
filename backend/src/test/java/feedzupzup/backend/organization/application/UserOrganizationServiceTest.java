package feedzupzup.backend.organization.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import feedzupzup.backend.feedback.application.ServiceIntegrationHelper;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.dto.UserOrganizationResponse;
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
            final Organization organization = new Organization("우아한테크코스");
            final Organization savedOrganization = organizationRepository.save(organization);

            // when
            final UserOrganizationResponse response = userOrganizationService.getOrganizationById(savedOrganization.getId());

            // then
            assertThat(response.organizationName()).isEqualTo("우아한테크코스");
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

        @Test
        @DisplayName("다양한 단체 이름으로 조회가 가능하다")
        void get_organization_by_id_with_various_names() {
            // given
            final Organization organization1 = new Organization("Backend Study");
            final Organization organization2 = new Organization("Frontend Study");
            final Organization organization3 = new Organization("DevOps Study");
            
            final Organization savedOrganization1 = organizationRepository.save(organization1);
            final Organization savedOrganization2 = organizationRepository.save(organization2);
            final Organization savedOrganization3 = organizationRepository.save(organization3);

            // when
            final UserOrganizationResponse response1 = userOrganizationService.getOrganizationById(savedOrganization1.getId());
            final UserOrganizationResponse response2 = userOrganizationService.getOrganizationById(savedOrganization2.getId());
            final UserOrganizationResponse response3 = userOrganizationService.getOrganizationById(savedOrganization3.getId());

            // then
            assertThat(response1.organizationName()).isEqualTo("Backend Study");
            assertThat(response2.organizationName()).isEqualTo("Frontend Study");
            assertThat(response3.organizationName()).isEqualTo("DevOps Study");
        }

        @Test
        @DisplayName("긴 단체 이름도 정상적으로 조회된다")
        void get_organization_by_id_with_long_name() {
            // given
            final String longOrganizationName = "매우 긴 단체 이름을 가진 스터디 단체입니다 - Backend Development Study Organization";
            final Organization organization = new Organization(longOrganizationName);
            final Organization savedOrganization = organizationRepository.save(organization);

            // when
            final UserOrganizationResponse response = userOrganizationService.getOrganizationById(savedOrganization.getId());

            // then
            assertThat(response.organizationName()).isEqualTo(longOrganizationName);
        }

        @Test
        @DisplayName("특수문자가 포함된 단체 이름도 정상적으로 조회된다")
        void get_organization_by_id_with_special_characters() {
            // given
            final String specialCharacterOrganizationName = "C++ & Java Study (2024)";
            final Organization organization = new Organization(specialCharacterOrganizationName);
            final Organization savedOrganization = organizationRepository.save(organization);

            // when
            final UserOrganizationResponse response = userOrganizationService.getOrganizationById(savedOrganization.getId());

            // then
            assertThat(response.organizationName()).isEqualTo(specialCharacterOrganizationName);
        }
    }
}
