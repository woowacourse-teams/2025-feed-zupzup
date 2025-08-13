package feedzupzup.backend.auth.dto;

import feedzupzup.backend.admin.domain.Admin;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "관리자 로그인 정보 응답")
public record AdminLoginResponse(
        @Schema(description = "로그인 ID", example = "admin123")
        String loginId,
        
        @Schema(description = "관리자 이름", example = "관리자")
        String adminName,
        
        @Schema(description = "관리자 ID (PK)", example = "1")
        Long adminId
) {
    
    public static AdminLoginResponse from(final Admin admin) {
        return new AdminLoginResponse(
                admin.getLoginId().getValue(),
                admin.getAdminName().getValue(),
                admin.getId()
        );
    }
}
