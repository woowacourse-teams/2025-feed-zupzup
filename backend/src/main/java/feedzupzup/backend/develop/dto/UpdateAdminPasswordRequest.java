package feedzupzup.backend.develop.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "어드민 비밀번호 변경 요청")
public record UpdateAdminPasswordRequest(

        @Schema(description = "관리자 비밀번호", example = "top-secret")
        String developerPassword,

        @Schema(description = "어드민 ID", example = "1")
        Long adminId,

        @Schema(description = "변경 할 비밀번호", example = "password123")
        String changePasswordValue
) {

}
