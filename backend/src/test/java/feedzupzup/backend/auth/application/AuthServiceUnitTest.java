package feedzupzup.backend.auth.application;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.admin.domain.vo.AdminName;
import feedzupzup.backend.admin.domain.vo.EncodedPassword;
import feedzupzup.backend.admin.domain.vo.LoginId;
import feedzupzup.backend.admin.dto.AdminSession;
import feedzupzup.backend.auth.dto.request.LoginRequest;
import feedzupzup.backend.auth.dto.request.SignUpRequest;
import feedzupzup.backend.auth.exception.AuthException;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.global.response.ErrorCode;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceUnitTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ActiveSessionStore activeSessionStore;

    @Nested
    @DisplayName("회원가입 예외 테스트")
    class SignUpExceptionTest {

        @Test
        @DisplayName("중복된 로그인 ID가 존재하면 예외가 발생한다")
        void signUp_DuplicateLoginId() {
            // given
            SignUpRequest request = new SignUpRequest("testId", "password123", "testName");

            given(adminRepository.existsByLoginId(new LoginId("testId"))).willReturn(true);

            // when & then
            assertThatThrownBy(() -> authService.signUp(request))
                    .isInstanceOf(AuthException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_LOGIN_ID);
        }

        @Test
        @DisplayName("유효하지 않은 비밀번호 형식이면 예외가 발생한다")
        void signUp_InvalidPasswordFormat() {
            // given
            SignUpRequest request = new SignUpRequest("testId", "password한글", "testName");

            given(adminRepository.existsByLoginId(any())).willReturn(false);

            // when & then
            assertThatCode(() -> authService.signUp(request))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("짧은 비밀번호면 예외가 발생한다")
        void signUp_ShortPassword() {
            // given
            SignUpRequest request = new SignUpRequest("testId", "test", "testName");

            given(adminRepository.existsByLoginId(any())).willReturn(false);

            // when & then
            assertThatCode(() -> authService.signUp(request))
                    .isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("로그인 예외 테스트")
    class LoginExceptionTest {

        @Test
        @DisplayName("존재하지 않는 로그인 ID면 예외가 발생한다")
        void login_LoginIdNotFound() {
            // given
            LoginRequest request = new LoginRequest("noExistId", "password123");

            given(adminRepository.findByLoginId(new LoginId("noExistId"))).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("비밀번호가 일치하지 않으면 예외가 발생한다")
        void login_InvalidPassword() {
            // given
            EncodedPassword correctPassword = new EncodedPassword("correctPassword123");
            Admin admin = new Admin(
                    new LoginId("loginId2"),
                    correctPassword,
                    new AdminName("testName")
            );

            LoginRequest request = new LoginRequest("loginId2", "wrongPassword123");

            given(adminRepository.findByLoginId(new LoginId("loginId2"))).willReturn(Optional.of(admin));
            given(passwordEncoder.matches("wrongPassword123", correctPassword.value())).willReturn(false);

            // when & then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(AuthException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_LOGIN_CREDENTIALS);
        }
    }

    @Nested
    @DisplayName("관리자 정보 조회 예외 테스트")
    class GetAdminLoginInfoExceptionTest {

        @Test
        @DisplayName("존재하지 않는 관리자 ID면 예외가 발생한다")
        void getAdminLoginInfo_AdminNotFound() {
            // given
            AdminSession adminSession = new AdminSession(999L);

            given(adminRepository.findById(999L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> authService.getAdminLoginInfo(adminSession))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Test
    @DisplayName("로그아웃 시 세션 정보가 삭제된다.")
    void logout_session_delete_test() {
        // given
        final AdminSession adminSession = new AdminSession(1L);

        // when
        authService.logout(adminSession);

        // then
        verify(activeSessionStore).removeActiveSession(adminSession.adminId());
    }
}
