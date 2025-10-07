package feedzupzup.backend.auth.presentation.resolver;

import static feedzupzup.backend.global.util.CookieUtilization.*;

import com.google.common.net.HttpHeaders;
import feedzupzup.backend.auth.presentation.annotation.Visitor;
import feedzupzup.backend.global.util.CurrentDateTime;
import feedzupzup.backend.guest.domain.guest.Guest;
import feedzupzup.backend.guest.domain.guest.GuestRepository;
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

    private final GuestRepository guestRepository;

    @Override
    public Object resolveArgument(
            final MethodParameter parameter,
            final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest,
            final WebDataBinderFactory binderFactory
    ) throws Exception {
        final HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        final HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        final Optional<UUID> visitorId = getVisitorIdFromCookie(request);

        if (visitorId.isEmpty()) {
            UUID newId = UUID.randomUUID();
            final ResponseCookie cookie = createCookie(VISITOR_KEY, newId);
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            return new Guest(newId, CurrentDateTime.create());
        }

        final UUID visitorUuid = visitorId.get();
        return guestRepository.findByVisitorUuid(visitorUuid)
                .orElseGet(() -> new Guest(visitorUuid, CurrentDateTime.create()));
    }

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Visitor.class) &&
                parameter.getParameterType().equals(Guest.class);
    }
}
