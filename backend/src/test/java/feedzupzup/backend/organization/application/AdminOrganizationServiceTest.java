package feedzupzup.backend.organization.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.admin.domain.fixture.AdminFixture;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.global.exception.UnAuthorizeException.InvalidAuthorizeException;
import feedzupzup.backend.organization.dto.request.CreateOrganizationRequest;
import feedzupzup.backend.organization.dto.request.UpdateOrganizationRequest;
import feedzupzup.backend.organization.dto.response.AdminCreateOrganizationResponse;
import feedzupzup.backend.organization.dto.response.AdminUpdateOrganizationResponse;
import java.util.Set;
import java.util.UUID;
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
                        Set.of("건의", "신고")
                );

        final Admin savedAdmin = createAndSaveAdmin();

        final AdminCreateOrganizationResponse response = adminOrganizationService.createOrganization(
                createOrganizationRequest, savedAdmin.getId());

        assertThat(response.organizationUuid()).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 admin이 조직을 생성하려고 한다면, 예외가 발생해야 한다.")
    void invalid_admin_request_then_throw_exception() {
        CreateOrganizationRequest createOrganizationRequest =
                new CreateOrganizationRequest(
                        "우테코",
                        Set.of("건의", "신고")
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
                        Set.of("크롱크롱", "대나무헬리콥터")
                );
        final Admin admin = createAndSaveAdmin();
        assertThatThrownBy(() -> adminOrganizationService.createOrganization(createOrganizationRequest, admin.getId()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("권한이 있는 admin이 본인의 단체를 수정할 수 있어야 한다")
    void update_organization_success() {
        // given
        CreateOrganizationRequest request =
                new CreateOrganizationRequest(
                        "우테코",
                        Set.of("건의", "신고")
                );

        final Admin savedAdmin = createAndSaveAdmin();

        final AdminCreateOrganizationResponse createResponse = adminOrganizationService.createOrganization(
                request, savedAdmin.getId());

        UpdateOrganizationRequest updateOrganizationRequest = new UpdateOrganizationRequest(
                "우테코코코", Set.of("기타", "칭찬", "정보공유")
        );
        final UUID organizationUuid = UUID.fromString(createResponse.organizationUuid());

        // when
        final AdminUpdateOrganizationResponse updateResponse = adminOrganizationService.updateOrganization(
                organizationUuid,
                updateOrganizationRequest,
                savedAdmin.getId()
        );

        // then
        assertThat(updateResponse.updateName()).isEqualTo("우테코코코");
        assertThat(updateResponse.updateCategories()).containsExactlyInAnyOrder("기타", "칭찬", "정보공유");
    }

    @Test
    @DisplayName("단체 수정 권한이 없는 admin이 수정 요청을 보낼경우, 예외가 발생해야 한다")
    void update_organization_not_authorize_case() {
        // given
        final Admin savedAdmin = createAndSaveAdmin();
        final Admin otherAdmin = AdminFixture.createFromLoginId("otherAdmin");
        adminRepository.save(otherAdmin);

        CreateOrganizationRequest request =
                new CreateOrganizationRequest(
                        "우테코",
                        Set.of("건의", "신고")
                );

        final AdminCreateOrganizationResponse createResponse = adminOrganizationService.createOrganization(
                request, savedAdmin.getId());

        UpdateOrganizationRequest updateOrganizationRequest = new UpdateOrganizationRequest(
                "우테코코코", Set.of("기타", "칭찬", "정보공유")
        );
        final UUID organizationUuid = UUID.fromString(createResponse.organizationUuid());

        // when, then
        assertThatThrownBy(() -> adminOrganizationService.updateOrganization(
                organizationUuid, updateOrganizationRequest, otherAdmin.getId()))
                .isInstanceOf(InvalidAuthorizeException.class);
    }

    private Admin createAndSaveAdmin() {
        final Admin admin = AdminFixture.create();
        return adminRepository.save(admin);
    }
}
