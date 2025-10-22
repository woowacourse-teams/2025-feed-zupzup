package feedzupzup.backend.auth.application;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.admin.domain.vo.EncodedPassword;
import feedzupzup.backend.admin.domain.vo.LoginId;
import feedzupzup.backend.admin.dto.AdminSession;
import feedzupzup.backend.auth.dto.request.LoginRequest;
import feedzupzup.backend.auth.dto.request.SignUpRequest;
import feedzupzup.backend.auth.dto.response.LoginResponse;
import feedzupzup.backend.auth.dto.response.SignUpResponse;
import feedzupzup.backend.auth.exception.AuthException.DuplicateLoginIdException;
import feedzupzup.backend.auth.exception.AuthException.InvalidPasswordException;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final ActiveSessionStore activeSessionStore;

    public LoginResponse getAdminLoginInfo(final AdminSession adminSession) {
        final Admin admin = adminRepository.findById(adminSession.adminId())
                .orElseThrow(() -> new ResourceNotFoundException("관리자 정보를 찾을 수 없습니다. ID: " + adminSession.adminId()));

        activeSessionStore.addActiveSession(adminSession.adminId());
        return LoginResponse.from(admin);
    }

    @Transactional
    public SignUpResponse signUp(final SignUpRequest request) {
        validateDuplicateLoginId(request.toLoginId());
        final EncodedPassword encodedPassword = passwordEncoder.encode(request.toPassword());
        final Admin admin = request.toAdmin(encodedPassword);
        final Admin savedAdmin = adminRepository.save(admin);
        return SignUpResponse.from(savedAdmin);
    }

    private void validateDuplicateLoginId(final LoginId loginId) {
        if (adminRepository.existsByLoginId(loginId)) {
            throw new DuplicateLoginIdException("이미 존재하는 로그인 ID입니다: ");
        }
    }

    @Transactional
    public LoginResponse login(final LoginRequest request) {
        final LoginId loginId = new LoginId(request.loginId());

        final Admin admin = adminRepository.findByLoginId(loginId)
                .orElseThrow(() -> new ResourceNotFoundException("로그인 정보가 올바르지 않습니다"));

        if (!passwordEncoder.matches(request.password(), admin.getPasswordValue())) {
            throw new InvalidPasswordException("로그인 정보가 올바르지 않습니다");
        }

        activeSessionStore.addActiveSession(admin.getId());
        return LoginResponse.from(admin);
    }

    public void logout(final AdminSession adminSession) {
        activeSessionStore.removeActiveSession(adminSession.adminId());
    }

}
