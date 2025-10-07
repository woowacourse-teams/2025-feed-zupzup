package feedzupzup.backend.global.util;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;

@Slf4j
public class CookieUtilization {

    public static final String VISITOR_KEY = "visitorId";

    private CookieUtilization() {}

    public static ResponseCookie createCookie(String key, UUID value) {
        final ResponseCookie responseCookie = ResponseCookie.from(key, value.toString())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .build();
        log.info("쿠키 생성 완료 : {" + "key = " + key + ", value = " + value + "}");
        return responseCookie;
    }

    public static Optional<UUID> getVisitorIdFromCookie(final HttpServletRequest request) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> VISITOR_KEY.equals(cookie.getName()))
                .findFirst()
                .map(cookie -> UUID.fromString(cookie.getValue()));
    }

}
