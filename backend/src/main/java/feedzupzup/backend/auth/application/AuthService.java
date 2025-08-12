package feedzupzup.backend.auth.application;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.admin.domain.vo.AdminName;
import feedzupzup.backend.admin.domain.vo.LoginId;
import feedzupzup.backend.admin.domain.vo.Password;
import feedzupzup.backend.admin.dto.AdminSession;
import feedzupzup.backend.auth.exception.AuthException;
import feedzupzup.backend.auth.dto.AdminLoginResponse;
import feedzupzup.backend.auth.dto.LoginRequest;
import feedzupzup.backend.auth.dto.SignUpRequest;
import feedzupzup.backend.auth.dto.SignUpResponse;
import feedzupzup.backend.auth.session.SessionManager;
import feedzupzup.backend.global.response.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final AdminRepository adminRepository;
    private final SessionManager sessionManager;

    public AdminLoginResponse getAdminLoginInfo(final AdminSession adminSession) {
        final Admin admin = adminRepository.findById(adminSession.adminId())
                .orElseThrow(() -> new AuthException(ErrorCode.ADMIN_NOT_LOGGED_IN, 
                        "관리자 정보를 찾을 수 없습니다. ID: " + adminSession.adminId()));
        
        return AdminLoginResponse.from(admin);
    }

    @Transactional
    public SignUpResponse signUp(final SignUpRequest request, final HttpServletRequest httpRequest) {
        validatePassword(request);
        final LoginId loginId = new LoginId(request.loginId());
        validateDuplicateLoginId(request, loginId);
        final Password password = new Password(request.password());
        final AdminName adminName = new AdminName(request.adminName());
        
        final Admin admin = new Admin(loginId, password, adminName);
        final Admin savedAdmin = adminRepository.save(admin);
        
        sessionManager.createAdminSession(httpRequest, savedAdmin.getId());
        
        return SignUpResponse.from(savedAdmin);
    }

    private void validateDuplicateLoginId(SignUpRequest request, LoginId loginId) {
        if (adminRepository.existsByLoginId(loginId)) {
            throw new AuthException(ErrorCode.DUPLICATE_LOGIN_ID,
                    "이미 존재하는 로그인 ID입니다: " + request.loginId());
        }
    }

    private static void validatePassword(SignUpRequest request) {
        if (!request.password().equals(request.passwordConfirm())) {
            throw new AuthException(ErrorCode.PASSWORD_NOT_MATCH,
                    "비밀번호와 비밀번호 확인이 일치하지 않습니다");
        }
    }

    @Transactional
    public AdminLoginResponse login(final LoginRequest request, final HttpServletRequest httpRequest) {
        final LoginId loginId = new LoginId(request.loginId());
        
        final Admin admin = adminRepository.findByLoginId(loginId)
                .orElseThrow(() -> new AuthException(ErrorCode.INVALID_LOGIN_CREDENTIALS,
                        "로그인 정보가 올바르지 않습니다"));

        if (!admin.isPasswordMatched(request.password())) {
            throw new AuthException(ErrorCode.INVALID_LOGIN_CREDENTIALS,
                    "로그인 정보가 올바르지 않습니다");
        }

        sessionManager.createAdminSession(httpRequest, admin.getId());
        
        return AdminLoginResponse.from(admin);
    }

    @Transactional
    public void logout(final HttpServletRequest httpRequest) {
        sessionManager.removeAdminSession(httpRequest);
    }
}
