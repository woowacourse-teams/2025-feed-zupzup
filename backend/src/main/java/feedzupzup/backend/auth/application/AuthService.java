package feedzupzup.backend.auth.application;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.admin.domain.vo.LoginId;
import feedzupzup.backend.admin.domain.vo.Password;
import feedzupzup.backend.admin.dto.AdminSession;
import feedzupzup.backend.auth.dto.AdminLoginResponse;
import feedzupzup.backend.auth.dto.LoginRequest;
import feedzupzup.backend.auth.dto.SignUpRequest;
import feedzupzup.backend.auth.dto.SignUpResponse;
import feedzupzup.backend.auth.encoder.PasswordEncoder;
import feedzupzup.backend.auth.exception.AuthException.DuplicateLoginIdException;
import feedzupzup.backend.auth.exception.AuthException.InvalidPasswordException;
import feedzupzup.backend.auth.exception.AuthException.PasswordNotMatchException;
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

    public AdminLoginResponse getAdminLoginInfo(final AdminSession adminSession) {
        final Admin admin = adminRepository.findById(adminSession.adminId())
                .orElseThrow(() -> new ResourceNotFoundException("관리자 정보를 찾을 수 없습니다. ID: " + adminSession.adminId()));

        return AdminLoginResponse.from(admin);
    }

    @Transactional
    public SignUpResponse signUp(final SignUpRequest request) {
        validatePassword(request);
        final LoginId loginId = new LoginId(request.loginId());
        validateDuplicateLoginId(loginId);
        final Password encodedPassword = Password.createEncodedPassword(request.password(), passwordEncoder);
        final Admin admin = request.toAdmin(encodedPassword);
        final Admin savedAdmin = adminRepository.save(admin);

        return SignUpResponse.from(savedAdmin);
    }

    private void validateDuplicateLoginId(final LoginId loginId) {
        if (adminRepository.existsByLoginId(loginId)) {
            throw new DuplicateLoginIdException("이미 존재하는 로그인 ID입니다: ");
        }
    }

    private static void validatePassword(final SignUpRequest request) {
        if (!request.password().equals(request.passwordConfirm())) {
            throw new PasswordNotMatchException("비밀번호와 비밀번호 확인이 일치하지 않습니다");
        }
    }

    @Transactional
    public AdminLoginResponse login(final LoginRequest request) {
        final LoginId loginId = new LoginId(request.loginId());

        final Admin admin = adminRepository.findByLoginId(loginId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "로그인 정보가 올바르지 않습니다"));

        if (!admin.getPassword().matches(request.password(), passwordEncoder)) {
            throw new InvalidPasswordException("로그인 정보가 올바르지 않습니다");
        }

        return AdminLoginResponse.from(admin);
    }

}
