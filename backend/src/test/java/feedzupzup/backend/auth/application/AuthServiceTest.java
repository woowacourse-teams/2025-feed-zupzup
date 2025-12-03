package feedzupzup.backend.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.admin.domain.vo.AdminName;
import feedzupzup.backend.admin.domain.vo.LoginId;
import feedzupzup.backend.admin.domain.vo.Password;
import feedzupzup.backend.admin.dto.AdminSession;
import feedzupzup.backend.auth.dto.request.LoginRequest;
import feedzupzup.backend.auth.dto.request.SignUpRequest;
import feedzupzup.backend.auth.dto.response.LoginResponse;
import feedzupzup.backend.auth.dto.response.SignUpResponse;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AuthServiceTest extends ServiceIntegrationHelper {

    @Autowired
    private AuthService authService;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Test
    @DisplayName("정상적인 회원가입 요청시 회원가입이 성공한다")
    void signUp_Success() {
        // Given
        SignUpRequest request = new SignUpRequest("testId", "password123", "testName");

        // When
        SignUpResponse response = authService.signUp(request);

        // Then
        assertAll(
            () -> assertThat(response.loginId()).isEqualTo("testId"),
            () -> assertThat(response.adminName()).isEqualTo("testName")
        );

        // Admin이 실제로 저장되었는지 확인
        Admin savedAdmin = adminRepository.findByLoginId(new LoginId("testId")).orElseThrow();
        assertThat(savedAdmin.getLoginId().value()).isEqualTo("testId");
        assertThat(savedAdmin.getAdminName().value()).isEqualTo("testName");
    }

    @Test
    @DisplayName("정상적인 로그인 요청시 로그인이 성공한다")
    void login_Success() {
        // Given
        final Password password = new Password("password123");
        Admin admin = new Admin(new LoginId("loginId"), passwordEncoder.encode(password), new AdminName("testName"));
        adminRepository.save(admin);

        LoginRequest request = new LoginRequest("loginId", "password123");

        // When
        LoginResponse response = authService.login(request);

        // Then
        assertAll(
            () -> assertThat(response.loginId()).isEqualTo("loginId"),
            () -> assertThat(response.adminName()).isEqualTo("testName")
        );
    }


    @Test
    @DisplayName("로그인된 관리자 정보 조회가 성공한다")
    void getAdminLoginInfo_Success() {
        // Given
        Password password = new Password("password123");
        Admin admin = new Admin(new LoginId("adminId"), passwordEncoder.encode(password), new AdminName("testName"));
        Admin savedAdmin = adminRepository.save(admin);

        AdminSession adminSession = new AdminSession(savedAdmin.getId());

        // When
        LoginResponse response = authService.getAdminLoginInfo(adminSession);

        // Then
        assertAll(
            () -> assertThat(response.loginId()).isEqualTo("adminId"),
            () -> assertThat(response.adminName()).isEqualTo("testName")
        );
    }
}
