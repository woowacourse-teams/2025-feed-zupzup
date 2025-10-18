package feedzupzup.backend.auth.presentation.resolver;

import static feedzupzup.backend.auth.presentation.constants.RequestAttribute.ADMIN_ID;
import static feedzupzup.backend.auth.presentation.constants.RequestAttribute.ORGANIZATION_ID;

import feedzupzup.backend.auth.presentation.annotation.LoginOrganizer;
import feedzupzup.backend.organizer.dto.LoginOrganizerInfo;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class OrganizerArgumentResolver implements HandlerMethodArgumentResolver {

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
        final HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        final UUID organizationUuid = (UUID) request.getAttribute(ORGANIZATION_ID.getValue());
        final Long adminId = (Long) request.getAttribute(ADMIN_ID.getValue());

        return new LoginOrganizerInfo(adminId, organizationUuid);
    }
}
