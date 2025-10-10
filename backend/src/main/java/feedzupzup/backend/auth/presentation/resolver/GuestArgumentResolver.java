package feedzupzup.backend.auth.presentation.resolver;

import static feedzupzup.backend.global.util.CookieUtilization.*;

import com.google.common.net.HttpHeaders;
import feedzupzup.backend.auth.presentation.annotation.SavedGuest;
import feedzupzup.backend.auth.presentation.annotation.VisitedGuest;
import feedzupzup.backend.global.util.CookieUtilization;
import feedzupzup.backend.guest.application.GuestService;
import feedzupzup.backend.guest.dto.GuestInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class GuestArgumentResolver implements HandlerMethodArgumentResolver {

    private final GuestService guestService;
    private final CookieUtilization cookieUtilization;

    @Override
    public Object resolveArgument(
            final MethodParameter parameter,
            final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest,
            final WebDataBinderFactory binderFactory
    ) throws Exception {
        final HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        final HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        final Optional<UUID> guestId = cookieUtilization.getGuestIdFromCookie(request);

        if (guestId.isEmpty()) {
            UUID newId = UUID.randomUUID();
            final ResponseCookie cookie = cookieUtilization.createCookie(GUEST_KEY, newId);
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            if (parameter.hasParameterAnnotation(SavedGuest.class)) {
                guestService.save(newId);
            }
            return new GuestInfo(newId);
        }

        if (!guestService.isSavedGuest(guestId.get())) {
            guestService.save(guestId.get());
        }
        return new GuestInfo(guestId.get());
    }

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        final boolean isSupportAnnotation = parameter.hasParameterAnnotation(VisitedGuest.class) ||
                parameter.hasParameterAnnotation(SavedGuest.class);
        final boolean isSupportType = parameter.getParameterType().equals(GuestInfo.class);

        return isSupportAnnotation && isSupportType;
    }
}
