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
import feedzupzup.backend.auth.dto.AdminLoginResponse;
import feedzupzup.backend.auth.dto.LoginRequest;
import feedzupzup.backend.auth.dto.SignUpRequest;
import feedzupzup.backend.auth.dto.SignUpResponse;
import feedzupzup.backend.auth.encoder.PasswordEncoder;
import feedzupzup.backend.auth.exception.AuthException;
import feedzupzup.backend.config.ServiceIntegrationHelper;
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
        SignUpRequest request = new SignUpRequest("testId", "password123", "password123", "testName");

        // When
        SignUpResponse response = authService.signUp(request);

        // Then
        assertAll(
            () -> assertThat(response.loginId()).isEqualTo("testId"),
            () -> assertThat(response.adminName()).isEqualTo("testName")
        );
        
        // Admin이 실제로 저장되었는지 확인
        Admin savedAdmin = adminRepository.findByLoginId(new LoginId("testId")).orElseThrow();
        assertThat(savedAdmin.getLoginId().getValue()).isEqualTo("testId");
        assertThat(savedAdmin.getAdminName().getValue()).isEqualTo("testName");
    }

    @Test
    @DisplayName("비밀번호와 비밀번호 확인이 일치하지 않으면 예외가 발생한다")
    void signUp_PasswordNotMatch() {
        // Given
        SignUpRequest request = new SignUpRequest("testId", "password123", "differentPassword123", "testName");

        // When & Then
        assertThatThrownBy(() -> authService.signUp(request))
                .isInstanceOf(AuthException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PASSWORD_NOT_MATCH);
    }

    @Test
    @DisplayName("중복된 로그인 ID가 존재하면 예외가 발생한다")
    void signUp_DuplicateLoginId() {
        // Given
        Admin existingAdmin = new Admin(new LoginId("testId"), new Password("password123"), new AdminName("existName"));
        adminRepository.save(existingAdmin);
        
        SignUpRequest request = new SignUpRequest("testId", "password123", "password123", "testName");

        // When & Then
        assertThatThrownBy(() -> authService.signUp(request))
                .isInstanceOf(AuthException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_LOGIN_ID);
    }

    @Test
    @DisplayName("유효하지 않은 비밀번호 형식이면 예외가 발생한다")
    void signUp_InvalidPasswordFormat() {
        // Given
        SignUpRequest request = new SignUpRequest("testId", "password한글", "password한글", "testName");

        // When & Then
        assertThatCode(() -> authService.signUp(request))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("짧은 비밀번호면 예외가 발생한다")
    void signUp_ShortPassword() {
        // Given
        SignUpRequest request = new SignUpRequest("testId", "test", "test", "testName");

        // When & Then
        assertThatCode(() -> authService.signUp(request))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("정상적인 로그인 요청시 로그인이 성공한다")
    void login_Success() {
        // Given
        Admin admin = new Admin(new LoginId("loginId"), Password.createEncodedPassword("password123", passwordEncoder), new AdminName("testName"));
        adminRepository.save(admin);
        
        LoginRequest request = new LoginRequest("loginId", "password123");

        // When
        AdminLoginResponse response = authService.login(request);

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
                .isInstanceOf(AuthException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_LOGIN_CREDENTIALS);
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 예외가 발생한다")
    void login_InvalidPassword() {
        // Given
        Admin admin = new Admin(new LoginId("loginId2"), new Password("correctPassword123"), new AdminName("testName"));
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
        Admin admin = new Admin(new LoginId("adminId"), new Password("password123"), new AdminName("testName"));
        Admin savedAdmin = adminRepository.save(admin);
        
        AdminSession adminSession = new AdminSession(savedAdmin.getId());

        // When
        AdminLoginResponse response = authService.getAdminLoginInfo(adminSession);

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
                .isInstanceOf(AuthException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ADMIN_NOT_LOGGED_IN);
    }
}
