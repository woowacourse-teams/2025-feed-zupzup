package feedzupzup.backend.auth.presentation.interceptor;

import static feedzupzup.backend.auth.presentation.constants.RequestAttribute.GUEST_ID;
import static feedzupzup.backend.global.util.CookieUtilization.GUEST_KEY;

import com.google.common.net.HttpHeaders;
import feedzupzup.backend.auth.presentation.annotation.SavedGuest;
import feedzupzup.backend.auth.presentation.annotation.VisitedGuest;
import feedzupzup.backend.global.util.CookieUtilization;
import feedzupzup.backend.guest.application.GuestService;
import feedzupzup.backend.guest.domain.guest.GuestActiveTracker;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class GuestInterceptor implements HandlerInterceptor {

    private final GuestService guestService;
    private final CookieUtilization cookieUtilization;
    private final GuestActiveTracker guestActiveTracker;

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
        final Optional<MethodParameter> guestParameter = findGuestAnnotation(handlerMethod);

        if (!isGuestAnnotation(guestParameter)) {
            return true;
        }

        final Optional<UUID> guestId = cookieUtilization.getGuestIdFromCookie(request);

        if (guestId.isEmpty()) {
            final UUID newId = UUID.randomUUID();
            createAndAddCookie(response, newId);

            if(isSavedGuestAnnotation(guestParameter.get())) {
                guestService.save(newId);
            }
            request.setAttribute(GUEST_ID.getValue(), newId);
            return true;
        }

        final UUID guestUuid = guestId.get();
        if (isSavedGuestAnnotation(guestParameter.get()) && !isSavedGuest(guestUuid)) {
            guestService.save(guestUuid);
        }

        guestActiveTracker.trackActivity(guestUuid);

        createAndAddCookie(response, guestUuid);
        request.setAttribute(GUEST_ID.getValue(), guestUuid);
        return true;
    }

    private boolean isSavedGuest(final UUID guestUuid) {
        return guestService.isSavedGuest(guestUuid);
    }

    private void createAndAddCookie(final HttpServletResponse response, final UUID newId) {
        final ResponseCookie cookie = cookieUtilization.createCookie(GUEST_KEY, newId);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private Optional<MethodParameter> findGuestAnnotation(final HandlerMethod handlerMethod) {
        return Arrays.stream(handlerMethod.getMethodParameters())
                .filter(parameter ->
                        parameter.hasParameterAnnotation(SavedGuest.class) ||
                                parameter.hasParameterAnnotation(VisitedGuest.class))
                .findFirst();
    }

    private boolean isHandlerMethod(final Object handler) {
        return handler instanceof HandlerMethod;
    }

    private boolean isGuestAnnotation(final Optional<MethodParameter> guestParameter) {
        return guestParameter.isPresent();
    }

    private boolean isSavedGuestAnnotation(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(SavedGuest.class);
    }
}
