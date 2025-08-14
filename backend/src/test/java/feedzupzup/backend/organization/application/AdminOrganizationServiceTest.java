package feedzupzup.backend.organization.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.admin.domain.fixture.AdminFixture;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.organization.dto.request.CreateOrganizationRequest;
import feedzupzup.backend.organization.dto.response.AdminCreateOrganizationResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AdminOrganizationServiceTest extends ServiceIntegrationHelper {

    @Autowired
    private AdminOrganizationService adminOrganizationService;

    @Autowired
    private AdminRepository adminRepository;

    @Test
    @DisplayName("정상적인 admin이 조직을 생성하려고 할 때, 생성할 수 있어야 한다.")
    void save_success_case() {
        CreateOrganizationRequest createOrganizationRequest =
                new CreateOrganizationRequest(
                        "우테코",
                        List.of("건의", "신고")
                );

        final Admin savedAdmin = createAndSaveAdmin();

        final AdminCreateOrganizationResponse response = adminOrganizationService.createOrganization(
                createOrganizationRequest, savedAdmin.getId());

        assertThat(response.organizationId()).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 admin이 조직을 생성하려고 한다면, 예외가 발생해야 한다.")
    void invalid_admin_request_then_throw_exception() {
        CreateOrganizationRequest createOrganizationRequest =
                new CreateOrganizationRequest(
                        "우테코",
                        List.of("건의", "신고")
                );

        assertThatThrownBy(() -> adminOrganizationService.createOrganization(createOrganizationRequest, 999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 카테고리를 생성하려고 한다면, 예외가 발생해야 한다.")
    void invalid_category_then_throw_exception() {
        CreateOrganizationRequest createOrganizationRequest =
                new CreateOrganizationRequest(
                        "우테코",
                        List.of("크롱크롱", "대나무헬리콥터")
                );
        final Admin admin = createAndSaveAdmin();
        assertThatThrownBy(() -> adminOrganizationService.createOrganization(createOrganizationRequest, admin.getId()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    private Admin createAndSaveAdmin() {
        final Admin admin = AdminFixture.create();
        return adminRepository.save(admin);
    }
}
