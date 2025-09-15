package feedzupzup.backend.auth.presentation.api;

import feedzupzup.backend.global.response.SuccessResponse;
import feedzupzup.backend.global.util.CookieUtilization;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "User API", description = "사용자 관련 API")
public interface UserApi {

    @Operation(summary = "사용자 쿠키 발급", description = "사용자 쿠키를 발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이미 쿠키가 존재해, 발급 하지 않음", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "201", description = "쿠키 발급 성공", useReturnTypeSchema = true)
    })
    @PostMapping("/user/set-cookie")
    ResponseEntity<SuccessResponse<Void>> setCookie(
            final HttpServletResponse response,
            @Parameter(hidden = true) @CookieValue(name = CookieUtilization.VISITOR_KEY, required = false) String visitorId
    );
}
