package feedzupzup.backend.admin.api;

import static org.springframework.http.HttpStatus.*;

import feedzupzup.backend.admin.dto.AdminSession;
import feedzupzup.backend.auth.presentation.annotation.AdminAuthenticationPrincipal;
import feedzupzup.backend.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "Admin", description = "관리자 API")
public interface AdminApi {

    @Operation(summary = "관리자 회원탈퇴", description = "관리자 회원탈퇴를 진행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "회원탈퇴 성공", useReturnTypeSchema = true)
    })
    @SecurityRequirement(name = "SessionAuth")
    @ResponseStatus(NO_CONTENT)
    @DeleteMapping("/admin")
    SuccessResponse<Void> withdraw(
            @Parameter(hidden = true) HttpServletRequest httpRequest,
            @Parameter(hidden = true) @AdminAuthenticationPrincipal AdminSession adminSession
    );
}
