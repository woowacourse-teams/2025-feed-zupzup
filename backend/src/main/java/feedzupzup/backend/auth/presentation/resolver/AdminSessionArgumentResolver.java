package feedzupzup.backend.auth.presentation.resolver;

import feedzupzup.backend.admin.dto.AdminSession;
import feedzupzup.backend.auth.presentation.annotation.AdminAuthenticationPrincipal;
import feedzupzup.backend.auth.presentation.session.HttpSessionManager;
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

    private final HttpSessionManager sessionManager;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AdminAuthenticationPrincipal.class) &&
               parameter.getParameterType().equals(AdminSession.class);
    }

    @Override
    public Object resolveArgument(final MethodParameter parameter, 
                                  final ModelAndViewContainer mavContainer,
                                  final NativeWebRequest webRequest, 
                                  final WebDataBinderFactory binderFactory) {
        final HttpServletRequest request = extractHttpServletRequest(webRequest);
        return sessionManager.getAdminSession(request);
    }

    private HttpServletRequest extractHttpServletRequest(final NativeWebRequest webRequest) {
        return webRequest.getNativeRequest(HttpServletRequest.class);
    }
}
