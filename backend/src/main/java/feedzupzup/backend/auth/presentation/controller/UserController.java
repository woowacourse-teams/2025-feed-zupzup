package feedzupzup.backend.auth.presentation.controller;

import com.google.common.net.HttpHeaders;
import feedzupzup.backend.auth.presentation.api.UserApi;
import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.global.util.CookieUtilization;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

    @Override
    public ResponseEntity<SuccessResponse<Void>> setCookie(
            final HttpServletResponse response,
            final String visitorId
    ) {
        if (visitorId == null) {
            final ResponseCookie cookie = CookieUtilization.createCookie(
                    CookieUtilization.VISITOR_KEY,
                    UUID.randomUUID()
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(SuccessResponse.success(HttpStatus.CREATED));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.success(HttpStatus.OK));
    }
}
