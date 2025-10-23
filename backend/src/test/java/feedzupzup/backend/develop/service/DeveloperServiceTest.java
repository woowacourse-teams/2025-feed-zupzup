package feedzupzup.backend.develop.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.admin.domain.fixture.AdminFixture;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.develop.dto.UpdateAdminPasswordRequest;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class DeveloperServiceTest extends ServiceIntegrationHelper {

    @Autowired
    private DeveloperService developerService;

    @Autowired
    private AdminRepository adminRepository;

    @Test
    @DisplayName("관리자 비밀번호 변경에 성공한다")
    void changePassword_success() {
        // given
        Admin admin = AdminFixture.create();
        Admin savedAdmin = adminRepository.save(admin);
        String originalPassword = savedAdmin.getPasswordValue();

        UpdateAdminPasswordRequest request = new UpdateAdminPasswordRequest(
                "any-value",
                savedAdmin.getLoginId().value(),
                "newPassword789"
        );

        // when
        developerService.changePassword(request);

        // then
        Admin updatedAdmin = adminRepository.findById(savedAdmin.getId()).get();
        assertThat(updatedAdmin.getPasswordValue()).isNotEqualTo(originalPassword);
    }

    @Test
    @DisplayName("존재하지 않는 관리자 ID로 비밀번호 변경 시 예외가 발생한다")
    void changePassword_fail_notFoundAdmin() {
        // given
        UpdateAdminPasswordRequest request = new UpdateAdminPasswordRequest(
                "any-value",
                "999L",
                "newPassword789"
        );

        // when & then
        assertThatThrownBy(() -> developerService.changePassword(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("존재하지 않는 adminId 입니다.");
    }
}

