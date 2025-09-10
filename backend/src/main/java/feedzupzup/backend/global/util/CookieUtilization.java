package feedzupzup.backend.global.util;

import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CookieUtilization {

    public static final String VISITOR_KEY = "visitorId";

    private CookieUtilization() {}

    // TODO : cookie 설정에 대해 공부해보기 ex) path
    public static Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        log.info("쿠키 생성 완료 : {" + "key = " + key + ", value = " + value + "}");
        return cookie;
    }
}
