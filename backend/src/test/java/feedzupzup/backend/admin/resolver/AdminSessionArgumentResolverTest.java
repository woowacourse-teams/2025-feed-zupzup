package feedzupzup.backend.admin.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import feedzupzup.backend.admin.dto.AdminSession;
import feedzupzup.backend.auth.annotation.AdminAuthenticationPrincipal;
import feedzupzup.backend.auth.resolver.AdminSessionArgumentResolver;
import feedzupzup.backend.auth.session.SessionManager;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;

@ExtendWith(MockitoExtension.class)
class AdminSessionArgumentResolverTest {

    @Mock
    private SessionManager sessionManager;

    @Mock
    private MethodParameter methodParameter;

    @Mock
    private NativeWebRequest nativeWebRequest;

    @Mock
    private HttpServletRequest httpServletRequest;

    private AdminSessionArgumentResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new AdminSessionArgumentResolver(sessionManager);
    }

    @Test
    @DisplayName("AdminAuthenticationPrincipal 애노테이션과 AdminSession 타입을 지원한다")
    void supportsParameter_WithCorrectAnnotationAndType_ReturnsTrue() {
        // given
        given(methodParameter.hasParameterAnnotation(AdminAuthenticationPrincipal.class)).willReturn(true);
        given(methodParameter.getParameterType()).willReturn((Class) AdminSession.class);

        // when
        final boolean result = resolver.supportsParameter(methodParameter);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("잘못된 애노테이션이면 지원하지 않는다")
    void supportsParameter_WithWrongAnnotation_ReturnsFalse() {
        // given
        given(methodParameter.hasParameterAnnotation(AdminAuthenticationPrincipal.class)).willReturn(false);

        // when
        final boolean result = resolver.supportsParameter(methodParameter);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("세션에서 AdminSession을 성공적으로 추출한다")
    void resolveArgument_WithValidSession_ReturnsAdminSession() {
        // given
        final AdminSession expectedSession = new AdminSession(1L);
        given(nativeWebRequest.getNativeRequest(HttpServletRequest.class)).willReturn(httpServletRequest);
        given(sessionManager.getAdminSession(httpServletRequest)).willReturn(expectedSession);

        // when
        final Object result = resolver.resolveArgument(methodParameter, null, nativeWebRequest, null);

        // then
        assertThat(result).isEqualTo(expectedSession);
    }
}
