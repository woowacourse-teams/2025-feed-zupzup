package feedzupzup.backend.develop.service;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.AdminRepository;
import feedzupzup.backend.admin.domain.vo.EncodedPassword;
import feedzupzup.backend.admin.domain.vo.Password;
import feedzupzup.backend.auth.application.PasswordEncoder;
import feedzupzup.backend.develop.dto.UpdateAdminPasswordRequest;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DeveloperService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void changePassword(final UpdateAdminPasswordRequest request) {
        Admin admin = adminRepository.findById(request.adminId())
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 adminId 입니다."));
        final EncodedPassword newPassword = passwordEncoder.encode(
                new Password(request.changePasswordValue()));
        admin.changePassword(newPassword);
    }
}
