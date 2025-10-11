package feedzupzup.backend.auth.presentation.resolver;

import feedzupzup.backend.auth.presentation.annotation.SavedGuest;
import feedzupzup.backend.auth.presentation.annotation.VisitedGuest;
import feedzupzup.backend.guest.dto.GuestInfo;
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
public class GuestArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public Object resolveArgument(
            final MethodParameter parameter,
            final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest,
            final WebDataBinderFactory binderFactory
    ) throws Exception {
        final HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        final UUID guestId = (UUID) request.getAttribute("guestId");
        return new GuestInfo(guestId);
    }

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        final boolean isSupportAnnotation =
                parameter.hasParameterAnnotation(VisitedGuest.class) ||
                parameter.hasParameterAnnotation(SavedGuest.class);

        final boolean isSupportType = parameter.getParameterType().equals(GuestInfo.class);

        return isSupportAnnotation && isSupportType;
    }
}
