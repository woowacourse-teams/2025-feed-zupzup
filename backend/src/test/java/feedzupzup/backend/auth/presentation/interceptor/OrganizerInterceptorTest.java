package feedzupzup.backend.auth.presentation.interceptor;

import static feedzupzup.backend.auth.presentation.constants.RequestAttribute.ADMIN_ID;
import static feedzupzup.backend.auth.presentation.constants.RequestAttribute.ORGANIZATION_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import feedzupzup.backend.admin.dto.AdminSession;
import feedzupzup.backend.auth.exception.AuthException.ForbiddenException;
import feedzupzup.backend.auth.exception.AuthException.UnauthorizedException;
import feedzupzup.backend.auth.presentation.annotation.LoginOrganizer;
import feedzupzup.backend.auth.presentation.session.HttpSessionManager;
import feedzupzup.backend.organizer.domain.OrganizerRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

@ExtendWith(MockitoExtension.class)
class OrganizerInterceptorTest {

    @Mock
    private HttpSessionManager sessionManager;

    @Mock
    private OrganizerRepository organizerRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HandlerMethod handlerMethod;

    @Mock
    private MethodParameter methodParameter;

    @InjectMocks
    private OrganizerInterceptor organizerInterceptor;

    @Test
    @DisplayName("Handler가 HandlerMethod가 아니면 true를 반환한다")
    void preHandle_NotHandlerMethod_ReturnsTrue() throws Exception {
        // given
        Object handler = new Object();

        // when
        boolean result = organizerInterceptor.preHandle(request, response, handler);

        // then
        assertThat(result).isTrue();
        verify(sessionManager, never()).getAdminSession(request);
    }

    @Test
    @DisplayName("LoginOrganizer 어노테이션이 없으면 true를 반환한다")
    void preHandle_NoLoginOrganizerAnnotation_ReturnsTrue() throws Exception {
        // given
        MethodParameter[] parameters = new MethodParameter[0];
        given(handlerMethod.getMethodParameters()).willReturn(parameters);

        // when
        boolean result = organizerInterceptor.preHandle(request, response, handlerMethod);

        // then
        assertThat(result).isTrue();
        verify(sessionManager, never()).getAdminSession(request);
    }

    @Nested
    @DisplayName("LoginOrganizer 어노테이션이 있는 경우")
    class WithLoginOrganizerAnnotation {

        @Test
        @DisplayName("organizationUuid가 null이면 UnauthorizedException이 발생한다")
        void preHandle_NullOrganizationUuid_ThrowsUnauthorizedException() {
            // given
            Long adminId = 1L;
            given(methodParameter.hasParameterAnnotation(LoginOrganizer.class)).willReturn(true);
            given(handlerMethod.getMethodParameters()).willReturn(new MethodParameter[]{methodParameter});
            given(sessionManager.getAdminSession(request)).willReturn(new AdminSession(adminId));
            given(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
                    .willReturn(Map.of());

            // when & then
            assertThatThrownBy(() -> organizerInterceptor.preHandle(request, response, handlerMethod))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessageContaining("잘못된 요청입니다.");
        }

        @Test
        @DisplayName("organizationUuid가 빈 문자열이면 UnauthorizedException이 발생한다")
        void preHandle_BlankOrganizationUuid_ThrowsUnauthorizedException() {
            // given
            Long adminId = 1L;
            given(methodParameter.hasParameterAnnotation(LoginOrganizer.class)).willReturn(true);
            given(handlerMethod.getMethodParameters()).willReturn(new MethodParameter[]{methodParameter});
            given(sessionManager.getAdminSession(request)).willReturn(new AdminSession(adminId));
            given(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
                    .willReturn(Map.of("organizationUuid", ""));

            // when & then
            assertThatThrownBy(() -> organizerInterceptor.preHandle(request, response, handlerMethod))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessageContaining("잘못된 요청입니다.");
        }

        @Test
        @DisplayName("관리자가 해당 단체에 대한 권한이 없으면 ForbiddenException이 발생한다")
        void preHandle_NoPermission_ThrowsForbiddenException() {
            // given
            Long adminId = 1L;
            UUID organizationUuid = UUID.randomUUID();

            given(methodParameter.hasParameterAnnotation(LoginOrganizer.class)).willReturn(true);
            given(handlerMethod.getMethodParameters()).willReturn(new MethodParameter[]{methodParameter});
            given(sessionManager.getAdminSession(request)).willReturn(new AdminSession(adminId));
            given(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
                    .willReturn(Map.of("organizationUuid", organizationUuid.toString()));
            given(organizerRepository.existsOrganizerByAdmin_IdAndOrganization_Uuid(adminId, organizationUuid))
                    .willReturn(false);

            // when & then
            assertThatThrownBy(() -> organizerInterceptor.preHandle(request, response, handlerMethod))
                    .isInstanceOf(ForbiddenException.class)
                    .hasMessageContaining("해당 단체: " + organizationUuid + "에 대한 접근 권한이 존재하지 않습니다.");
        }

        @Test
        @DisplayName("관리자가 해당 단체에 대한 권한이 있으면 true를 반환하고 request에 속성을 설정한다")
        void preHandle_WithPermission_ReturnsTrueAndSetsAttributes() throws Exception {
            // given
            Long adminId = 1L;
            UUID organizationUuid = UUID.randomUUID();

            given(methodParameter.hasParameterAnnotation(LoginOrganizer.class)).willReturn(true);
            given(handlerMethod.getMethodParameters()).willReturn(new MethodParameter[]{methodParameter});
            given(sessionManager.getAdminSession(request)).willReturn(new AdminSession(adminId));
            given(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
                    .willReturn(Map.of("organizationUuid", organizationUuid.toString()));
            given(organizerRepository.existsOrganizerByAdmin_IdAndOrganization_Uuid(adminId, organizationUuid))
                    .willReturn(true);

            // when
            boolean result = organizerInterceptor.preHandle(request, response, handlerMethod);

            // then
            assertThat(result).isTrue();
            verify(request).setAttribute(ORGANIZATION_ID.getValue(), organizationUuid);
            verify(request).setAttribute(ADMIN_ID.getValue(), adminId);
        }

        @Test
        @DisplayName("유효한 UUID 형식이 아니면 IllegalArgumentException이 발생한다")
        void preHandle_InvalidUuidFormat_ThrowsIllegalArgumentException() {
            // given
            Long adminId = 1L;
            String invalidUuid = "invalid-uuid";

            given(methodParameter.hasParameterAnnotation(LoginOrganizer.class)).willReturn(true);
            given(handlerMethod.getMethodParameters()).willReturn(new MethodParameter[]{methodParameter});
            given(sessionManager.getAdminSession(request)).willReturn(new AdminSession(adminId));
            given(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
                    .willReturn(Map.of("organizationUuid", invalidUuid));

            // when & then
            assertThatThrownBy(() -> organizerInterceptor.preHandle(request, response, handlerMethod))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}