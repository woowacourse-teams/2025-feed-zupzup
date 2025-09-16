package feedzupzup.backend.auth.presentation.controller;

import feedzupzup.backend.admin.dto.AdminSession;
import feedzupzup.backend.auth.application.AuthService;
import feedzupzup.backend.auth.dto.request.LoginRequest;
import feedzupzup.backend.auth.dto.request.SignUpRequest;
import feedzupzup.backend.auth.dto.response.LoginResponse;
import feedzupzup.backend.auth.dto.response.SignUpResponse;
import feedzupzup.backend.auth.presentation.api.AdminAuthApi;
import feedzupzup.backend.auth.presentation.session.HttpSessionManager;
import feedzupzup.backend.global.response.SuccessResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminAuthController implements AdminAuthApi {

    private final AuthService authService;
    private final HttpSessionManager httpSessionManager;

    @Override
    public SuccessResponse<SignUpResponse> signUp(final SignUpRequest request, final HttpServletRequest httpRequest) {
        final SignUpResponse response = authService.signUp(request);
        httpSessionManager.createAdminSession(httpRequest, response.adminId());
        return SuccessResponse.success(HttpStatus.CREATED, response);
    }

    @Override
    public SuccessResponse<LoginResponse> login(final LoginRequest request, final HttpServletRequest httpRequest) {
        final LoginResponse response = authService.login(request);
        httpSessionManager.createAdminSession(httpRequest, response.adminId());
        return SuccessResponse.success(HttpStatus.OK, response);
    }

    @Override
    public SuccessResponse<String> logout(final HttpServletRequest httpRequest) {
        final AdminSession adminSession = httpSessionManager.getAdminSession(httpRequest);
        authService.logout(adminSession.adminId());
        httpSessionManager.removeAdminSession(httpRequest);
        return SuccessResponse.success(HttpStatus.OK, "로그아웃이 완료되었습니다.");
    }

    @Override
    public SuccessResponse<LoginResponse> getAdminLoginStatus(final AdminSession adminSession) {
        final LoginResponse response = authService.getAdminLoginInfo(adminSession);
        return SuccessResponse.success(HttpStatus.OK, response);
    }
}
