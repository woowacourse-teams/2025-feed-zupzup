package feedzupzup.backend.auth.dto.response;

import feedzupzup.backend.admin.domain.Admin;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "관리자 회원가입 응답")
public record SignUpResponse(
        @Schema(description = "관리자 ID (PK)", example = "1")
        Long adminId,

        @Schema(description = "로그인 ID", example = "admin123")
        String loginId,

        @Schema(description = "관리자 이름", example = "관리자")
        String adminName
) {

    public static SignUpResponse from(final Admin admin) {
        return new SignUpResponse(
                admin.getId(),
                admin.getLoginId().getValue(),
                admin.getAdminName().getValue()
        );
    }
}
