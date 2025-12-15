package feedzupzup.backend.auth.presentation.session;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import feedzupzup.backend.auth.application.ActiveSessionStore;
import feedzupzup.backend.auth.exception.AuthException.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HttpSessionManagerTest {

    private String adminIdSessionKey = "adminId";

    private HttpSessionManager httpSessionManager;

    @Mock
    private ActiveSessionStore activeSessionStore;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @BeforeEach
    void setUp() {
        httpSessionManager = new HttpSessionManager(adminIdSessionKey, activeSessionStore);
    }

    @Test
    @DisplayName("세션이 존재하지 않으면 UnauthorizedException이 발생한다")
    void getAdminSession_NoSession_ThrowsUnauthorizedException() {
        // given
        given(request.getSession(false)).willReturn(null);

        // when & then
        assertThatThrownBy(() -> httpSessionManager.getAdminSession(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("세션을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("세션에 adminId가 없으면 UnauthorizedException이 발생한다")
    void getAdminSession_NoAdminIdInSession_ThrowsUnauthorizedException() {
        // given
        given(request.getSession(false)).willReturn(session);
        given(session.getAttribute(adminIdSessionKey)).willReturn(null);

        // when & then
        assertThatThrownBy(() -> httpSessionManager.getAdminSession(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("관리자가 로그인되어 있지 않습니다");
    }

}
