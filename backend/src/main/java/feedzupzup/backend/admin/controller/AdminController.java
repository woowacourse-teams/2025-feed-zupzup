package feedzupzup.backend.admin.controller;

import feedzupzup.backend.admin.api.AdminApi;
import feedzupzup.backend.admin.application.AdminService;
import feedzupzup.backend.admin.dto.AdminSession;
import feedzupzup.backend.auth.presentation.session.HttpSessionManager;
import feedzupzup.backend.global.response.SuccessResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminController implements AdminApi {

    private final AdminService adminService;
    private final HttpSessionManager httpSessionManager;

    @Override
    public SuccessResponse<Void> withdraw(final HttpServletRequest httpRequest, final AdminSession adminSession) {
        httpSessionManager.removeAdminSession(httpRequest);
        adminService.withdraw(adminSession.adminId());
        return SuccessResponse.success(HttpStatus.NO_CONTENT);
    }
}
