package feedzupzup.backend.auth.presentation.resolver;

import static feedzupzup.backend.auth.presentation.constants.RequestAttribute.ADMIN_ID;

import feedzupzup.backend.admin.dto.AdminSession;
import feedzupzup.backend.auth.presentation.annotation.AdminAuthenticationPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class AdminSessionArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AdminAuthenticationPrincipal.class) &&
                parameter.getParameterType().equals(AdminSession.class);
    }

    @Override
    public Object resolveArgument(
            final MethodParameter parameter,
            final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest,
            final WebDataBinderFactory binderFactory
    ) {
        final HttpServletRequest request = extractHttpServletRequest(webRequest);

        final Long adminId = (Long) request.getAttribute(ADMIN_ID.getValue());
        return new AdminSession(adminId);
    }

    private HttpServletRequest extractHttpServletRequest(final NativeWebRequest webRequest) {
        return webRequest.getNativeRequest(HttpServletRequest.class);
    }
}
