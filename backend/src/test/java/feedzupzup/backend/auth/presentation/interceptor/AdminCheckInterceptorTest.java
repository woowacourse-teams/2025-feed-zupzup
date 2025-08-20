package feedzupzup.backend.auth.presentation.interceptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.admin.dto.AdminSession;
import feedzupzup.backend.auth.exception.AuthException.UnauthorizedException;
import feedzupzup.backend.auth.presentation.session.HttpSessionManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminCheckInterceptorTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSessionManager httpSessionManager;

    @InjectMocks
    private AdminCheckInterceptor adminCheckInterceptor;

    @Test
    @DisplayName("세션에 adminId가 없으면 UnauthorizedException이 발생한다")
    void preHandle_NoAdminIdInSession() {
        // Given
        given(httpSessionManager.getAdminSession(any())).willReturn(new AdminSession(null));

        // When & Then
        assertThatThrownBy(() -> adminCheckInterceptor.preHandle(request, response, new Object()))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("adminId가 존재하지 않는 관리자면 UnauthorizeException이 발생한다")
    void preHandle_AdminNotExists() {
        // Given
        Long adminId = 1L;
        given(httpSessionManager.getAdminSession(any())).willReturn(new AdminSession(adminId));
        given(adminRepository.existsById(adminId)).willReturn(false);

        // When & Then
        assertThatThrownBy(() -> adminCheckInterceptor.preHandle(request, response, new Object()))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("해당 관리자 ID(adminId = " + adminId + ")에는 권한이 없습니다.");
    }

    @Test
    @DisplayName("유효한 adminId가 세션에 있고 관리자가 존재하면 true를 반환한다")
    void preHandle_Success() {
        // Given
        Long adminId = 1L;
        given(httpSessionManager.getAdminSession(any())).willReturn(new AdminSession(adminId));
        given(adminRepository.existsById(adminId)).willReturn(true);

        // When
        boolean result = adminCheckInterceptor.preHandle(request, response, new Object());

        // Then
        assertThat(result).isTrue();
    }
}
