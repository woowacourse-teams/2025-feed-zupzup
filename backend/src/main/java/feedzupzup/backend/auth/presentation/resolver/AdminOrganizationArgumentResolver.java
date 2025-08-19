package feedzupzup.backend.auth.presentation.resolver;

import feedzupzup.backend.auth.exception.AuthException.ForbiddenException;
import feedzupzup.backend.auth.exception.AuthException.UnauthorizedException;
import feedzupzup.backend.auth.presentation.annotation.LoginOrganizer;
import feedzupzup.backend.auth.presentation.session.HttpSessionManager;
import feedzupzup.backend.organizer.domain.OrganizerRepository;
import feedzupzup.backend.organizer.dto.LoginOrganizerInfo;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

@Component
@RequiredArgsConstructor
public class AdminOrganizationArgumentResolver implements HandlerMethodArgumentResolver {

    private final HttpSessionManager sessionManager;
    private final OrganizerRepository organizerRepository;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginOrganizer.class) &&
                parameter.getParameterType().equals(LoginOrganizerInfo.class);
    }

    @Override
    public Object resolveArgument(final MethodParameter parameter,
            final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest,
            final WebDataBinderFactory binderFactory
    ) throws Exception {
        final HttpServletRequest request = extractHttpServletRequest(webRequest);
        final Long adminId = sessionManager.getAdminSession(request).adminId();
        final UUID organizationUuid = extractOrganizationUuid(webRequest);
        if (!organizerRepository.existsOrganizerByAdmin_IdAndOrganization_Uuid(adminId, organizationUuid)) {
            throw new ForbiddenException("해당 기능에 대해 접근 권한이 존재하지 않습니다.");
        }
        return new LoginOrganizerInfo(adminId, organizationUuid);
    }

    private UUID extractOrganizationUuid(final NativeWebRequest webRequest) {
        final Map<String, String> uriTemplateVars = (Map<String, String>) webRequest.getAttribute(
                HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, NativeWebRequest.SCOPE_REQUEST);

        if (!uriTemplateVars.containsKey("organizationUuid")) {
            throw new UnauthorizedException("잘못된 요청입니다.");
        }

        final String organizationUuid = uriTemplateVars.get("organizationUuid");
        if (organizationUuid == null || organizationUuid.isBlank()) {
            throw new UnauthorizedException("잘못된 요청입니다.");
        }
        return UUID.fromString(organizationUuid);
    }

    private HttpServletRequest extractHttpServletRequest(final NativeWebRequest webRequest) {
        return webRequest.getNativeRequest(HttpServletRequest.class);
    }
}
