package feedzupzup.backend.auth.dto;

import feedzupzup.backend.admin.domain.Admin;
import feedzupzup.backend.admin.domain.vo.AdminName;
import feedzupzup.backend.admin.domain.vo.LoginId;
import feedzupzup.backend.admin.domain.vo.Password;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "관리자 회원가입 요청")
public record SignUpRequest(
        @Schema(description = "로그인 ID", example = "admin123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "로그인 ID는 필수입니다")
        String loginId,

        @Schema(description = "비밀번호", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "비밀번호는 필수입니다")
        String password,

        @Schema(description = "비밀번호 확인", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "비밀번호 확인은 필수입니다")
        String passwordConfirm,

        @Schema(description = "관리자 이름", example = "관리자", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "관리자 이름은 필수입니다")
        String adminName
) {
    public Admin toAdmin(final Password password) {
        return new Admin(new LoginId(this.loginId), password, new AdminName(this.adminName));
    }
}
