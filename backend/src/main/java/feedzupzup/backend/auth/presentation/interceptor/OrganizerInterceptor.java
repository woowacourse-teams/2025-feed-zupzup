package feedzupzup.backend.auth.presentation.interceptor;

import static feedzupzup.backend.auth.presentation.constants.RequestAttribute.ADMIN_ID;
import static feedzupzup.backend.auth.presentation.constants.RequestAttribute.ORGANIZATION_ID;

import feedzupzup.backend.auth.exception.AuthException.ForbiddenException;
import feedzupzup.backend.auth.exception.AuthException.UnauthorizedException;
import feedzupzup.backend.auth.presentation.annotation.LoginOrganizer;
import feedzupzup.backend.auth.presentation.session.HttpSessionManager;
import feedzupzup.backend.organizer.domain.OrganizerRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

@Component
@RequiredArgsConstructor
public class OrganizerInterceptor implements HandlerInterceptor {

    private final HttpSessionManager sessionManager;
    private final OrganizerRepository organizerRepository;

    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler
    ) throws Exception {

        if (!isHandlerMethod(handler)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        final Optional<MethodParameter> organizerParameter = findLoginOrganizerAnnotation(handlerMethod);

        if (organizerParameter.isEmpty()) {
            return true;
        }

        final Long adminId = sessionManager.getAdminSession(request).adminId();
        final UUID organizationUuid = extractOrganizationUuid(request);

        if (!organizerRepository.existsOrganizerByAdmin_IdAndOrganization_Uuid(adminId, organizationUuid)) {
            throw new ForbiddenException("해당 단체: " + organizationUuid + "에 대한 접근 권한이 존재하지 않습니다.");
        }

        request.setAttribute(ORGANIZATION_ID.getValue(), organizationUuid);
        request.setAttribute(ADMIN_ID.getValue(), adminId);

        return true;
    }

    private UUID extractOrganizationUuid(final HttpServletRequest request) {
        Map<String, String> uriTemplateVars =
                (Map<String, String>) request.getAttribute(
                        HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        final String organizationUuid = uriTemplateVars.get("organizationUuid");
        if (organizationUuid == null || organizationUuid.isBlank()) {
            throw new UnauthorizedException("잘못된 요청입니다.");
        }
        return UUID.fromString(organizationUuid);
    }

    private Optional<MethodParameter> findLoginOrganizerAnnotation(final HandlerMethod handlerMethod) {
        return Arrays.stream(handlerMethod.getMethodParameters())
                .filter(parameter ->
                        parameter.hasParameterAnnotation(LoginOrganizer.class))
                .findFirst();
    }

    private boolean isHandlerMethod(final Object handler) {
        return handler instanceof HandlerMethod;
    }
}
