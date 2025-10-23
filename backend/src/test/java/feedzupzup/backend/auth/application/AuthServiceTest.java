package feedzupzup.backend.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
import feedzupzup.backend.auth.exception.AuthException;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.global.response.ErrorCode;
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
    @DisplayName("중복된 로그인 ID가 존재하면 예외가 발생한다")
    void signUp_DuplicateLoginId() {
        // Given
        Password password = new Password("password123");
        Admin existingAdmin = new Admin(new LoginId("testId"), passwordEncoder.encode(password), new AdminName("existName"));
        adminRepository.save(existingAdmin);

        SignUpRequest request = new SignUpRequest("testId", "password123", "testName");

        // When & Then
        assertThatThrownBy(() -> authService.signUp(request))
                .isInstanceOf(AuthException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_LOGIN_ID);
    }

    @Test
    @DisplayName("유효하지 않은 비밀번호 형식이면 예외가 발생한다")
    void signUp_InvalidPasswordFormat() {
        // Given
        SignUpRequest request = new SignUpRequest("testId", "password한글", "testName");

        // When & Then
        assertThatCode(() -> authService.signUp(request))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("짧은 비밀번호면 예외가 발생한다")
    void signUp_ShortPassword() {
        // Given
        SignUpRequest request = new SignUpRequest("testId", "test", "testName");

        // When & Then
        assertThatCode(() -> authService.signUp(request))
                .isInstanceOf(RuntimeException.class);
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
    @DisplayName("존재하지 않는 로그인 ID면 예외가 발생한다")
    void login_LoginIdNotFound() {
        // Given
        LoginRequest request = new LoginRequest("noExistId", "password123");

        // When & Then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 예외가 발생한다")
    void login_InvalidPassword() {
        // Given
        Password password = new Password("correctPassword123");
        Admin admin = new Admin(new LoginId("loginId2"), passwordEncoder.encode(password), new AdminName("testName"));
        adminRepository.save(admin);

        LoginRequest request = new LoginRequest("loginId2", "wrongPassword123");

        // When & Then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(AuthException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_LOGIN_CREDENTIALS);
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

    @Test
    @DisplayName("존재하지 않는 관리자 ID면 예외가 발생한다")
    void getAdminLoginInfo_AdminNotFound() {
        // Given
        AdminSession adminSession = new AdminSession(999L);

        // When & Then
        assertThatThrownBy(() -> authService.getAdminLoginInfo(adminSession))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
