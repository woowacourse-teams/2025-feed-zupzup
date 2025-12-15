package feedzupzup.backend.auth.listener;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import feedzupzup.backend.auth.application.ActiveSessionStore;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminSessionListenerTest {

    private AdminSessionListener adminSessionListener;

    @Mock
    private ActiveSessionStore activeSessionStore;

    @Mock
    private HttpSessionEvent httpSessionEvent;

    @Mock
    private HttpSession httpSession;

    private static final String ADMIN_ID_SESSION_KEY = "ADMIN_ID";

    @BeforeEach
    void setUp() {
        adminSessionListener = new AdminSessionListener(activeSessionStore, ADMIN_ID_SESSION_KEY);
    }

    @Test
    @DisplayName("세션이 종료되고 adminId가 있으면 활성 세션에서 제거한다")
    void sessionDestroyed_WithAdminId_RemovesActiveSession() {
        // given
        Long adminId = 1L;
        given(httpSessionEvent.getSession()).willReturn(httpSession);
        given(httpSession.getAttribute(ADMIN_ID_SESSION_KEY)).willReturn(adminId);

        // when
        adminSessionListener.sessionDestroyed(httpSessionEvent);

        // then
        verify(activeSessionStore).removeActiveSession(adminId);
    }

    @Test
    @DisplayName("세션이 종료되고 adminId가 null이면 활성 세션에서 제거하지 않는다")
    void sessionDestroyed_WithNullAdminId_DoesNotRemoveActiveSession() {
        // given
        given(httpSessionEvent.getSession()).willReturn(httpSession);
        given(httpSession.getAttribute(ADMIN_ID_SESSION_KEY)).willReturn(null);

        // when
        adminSessionListener.sessionDestroyed(httpSessionEvent);

        // then
        verify(activeSessionStore, never()).removeActiveSession(null);
    }

    @Test
    @DisplayName("세션에 adminId가 존재하지 않으면 활성 세션에서 제거하지 않는다")
    void sessionDestroyed_NoAdminIdInSession_DoesNotRemoveActiveSession() {
        // given
        given(httpSessionEvent.getSession()).willReturn(httpSession);
        given(httpSession.getAttribute(ADMIN_ID_SESSION_KEY)).willReturn(null);

        // when
        adminSessionListener.sessionDestroyed(httpSessionEvent);

        // then
        verify(httpSession).getAttribute(ADMIN_ID_SESSION_KEY);
        verify(activeSessionStore, never()).removeActiveSession(null);
    }

    @Test
    @DisplayName("여러 관리자의 세션이 종료되면 각각 활성 세션에서 제거한다")
    void sessionDestroyed_MultipleAdmins_RemovesEachActiveSession() {
        // given
        Long adminId1 = 1L;
        Long adminId2 = 2L;

        // 첫 번째 세션 종료
        given(httpSessionEvent.getSession()).willReturn(httpSession);
        given(httpSession.getAttribute(ADMIN_ID_SESSION_KEY)).willReturn(adminId1);
        adminSessionListener.sessionDestroyed(httpSessionEvent);

        // 두 번째 세션 종료
        given(httpSession.getAttribute(ADMIN_ID_SESSION_KEY)).willReturn(adminId2);
        adminSessionListener.sessionDestroyed(httpSessionEvent);

        // then
        verify(activeSessionStore).removeActiveSession(adminId1);
        verify(activeSessionStore).removeActiveSession(adminId2);
    }
}