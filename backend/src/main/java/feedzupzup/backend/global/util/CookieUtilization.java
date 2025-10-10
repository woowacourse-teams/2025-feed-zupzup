package feedzupzup.backend.global.util;

import feedzupzup.backend.global.exception.BusinessViolationException.NotSupportedException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CookieUtilization {

    public static final String GUEST_KEY = "guestId";

    private final String cookieDomain;
    private final String sameSite;
    private final long maxAge;
    private final String path;
    private final boolean secure;

    public CookieUtilization(
            @Value("${cookie.domain}") final String cookieDomain,
            @Value("${cookie.sameSite}") final String sameSite,
            @Value("${cookie.max-age}") final long maxAge,
            @Value("${cookie.path}") final String path,
            @Value("${cookie.secure}") final boolean secure
    ) {
        this.cookieDomain = cookieDomain;
        this.sameSite = sameSite;
        this.maxAge = maxAge;
        this.path = path;
        this.secure = secure;
    }

    public ResponseCookie createCookie(String key, UUID value) {
        final ResponseCookie responseCookie = ResponseCookie.from(key, value.toString())
                .httpOnly(true)
                .domain(cookieDomain)
                .sameSite(sameSite)
                .maxAge(maxAge)
                .path(path)
                .secure(secure)
                .build();
        log.info("쿠키 생성 완료 : {" + "key = " + key + ", value = " + value + "}");
        return responseCookie;
    }

    public Optional<UUID> getGuestIdFromCookie(final HttpServletRequest request) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> GUEST_KEY.equals(cookie.getName()))
                .findFirst()
                .map(cookie -> parseUUID(cookie.getValue()));
    }

    private UUID parseUUID(String value) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new NotSupportedException("조작된 쿠키값이 사용된 요청입니다 :" + "value = " + value);
        }
    }

}
