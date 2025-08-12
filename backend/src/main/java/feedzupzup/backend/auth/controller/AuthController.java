package feedzupzup.backend.auth.controller;

import feedzupzup.backend.admin.dto.AdminSession;
import feedzupzup.backend.auth.api.AuthApi;
import feedzupzup.backend.auth.application.AuthService;
import feedzupzup.backend.auth.dto.AdminLoginResponse;
import feedzupzup.backend.auth.dto.LoginRequest;
import feedzupzup.backend.auth.dto.SignUpRequest;
import feedzupzup.backend.auth.dto.SignUpResponse;
import feedzupzup.backend.global.response.SuccessResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthService authService;

    @Override
    public SuccessResponse<SignUpResponse> signUp(final SignUpRequest request, final HttpServletRequest httpRequest) {
        final SignUpResponse response = authService.signUp(request, httpRequest);
        return SuccessResponse.success(HttpStatus.CREATED, response);
    }

    @Override
    public SuccessResponse<AdminLoginResponse> login(final LoginRequest request,
                                                     final HttpServletRequest httpRequest) {
        final AdminLoginResponse response = authService.login(request, httpRequest);
        return SuccessResponse.success(HttpStatus.OK, response);
    }

    @Override
    public SuccessResponse<String> logout(final HttpServletRequest httpRequest) {
        authService.logout(httpRequest);
        return SuccessResponse.success(HttpStatus.OK, "로그아웃이 완료되었습니다.");
    }

    @Override
    public SuccessResponse<AdminLoginResponse> getAdminLoginStatus(final AdminSession adminSession) {
        final AdminLoginResponse response = authService.getAdminLoginInfo(adminSession);
        return SuccessResponse.success(HttpStatus.OK, response);
    }
}
