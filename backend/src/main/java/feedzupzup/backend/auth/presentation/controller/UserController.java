package feedzupzup.backend.auth.presentation.controller;

import feedzupzup.backend.auth.presentation.api.UserApi;
import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.global.util.CookieUtilization;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

    @Override
    public SuccessResponse<Void> setCookie(
            final HttpServletResponse response,
            final String visitorId
    ) {
        if (visitorId == null) {
            final Cookie cookie = CookieUtilization.createCookie(
                    CookieUtilization.VISITOR_KEY,
                    UUID.randomUUID()
            );
            response.addCookie(cookie);
            return SuccessResponse.success(HttpStatus.CREATED);
        }
        return SuccessResponse.success(HttpStatus.OK);
    }
}
