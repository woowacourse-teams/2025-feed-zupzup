package feedzupzup.backend.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "관리자 회원가입 요청")
public record SignUpRequest(
        @Schema(description = "로그인 ID", example = "admin123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "로그인 ID는 필수입니다")
        @Size(min = 1, max = 10, message = "로그인 ID는 1~10글자여야 합니다")
        String loginId,

        @Schema(description = "비밀번호", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "비밀번호는 필수입니다")
        @Size(min = 5, message = "비밀번호는 5글자 이상이어야 합니다")
        String password,

        @Schema(description = "비밀번호 확인", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "비밀번호 확인은 필수입니다")
        String passwordConfirm,

        @Schema(description = "관리자 이름", example = "관리자", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "관리자 이름은 필수입니다")
        @Size(min = 1, max = 10, message = "관리자 이름은 1~10글자여야 합니다")
        String adminName
) {
}