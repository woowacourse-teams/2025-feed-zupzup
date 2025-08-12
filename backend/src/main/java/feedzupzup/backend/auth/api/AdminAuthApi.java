package feedzupzup.backend.auth.api;

import feedzupzup.backend.admin.dto.AdminSession;
import feedzupzup.backend.auth.annotation.AdminAuthenticationPrincipal;
import feedzupzup.backend.auth.dto.AdminLoginResponse;
import feedzupzup.backend.auth.dto.LoginRequest;
import feedzupzup.backend.auth.dto.SignUpRequest;
import feedzupzup.backend.auth.dto.SignUpResponse;
import feedzupzup.backend.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "인증", description = "관리자 인증 관련 API")
public interface AdminAuthApi {

    @Operation(summary = "관리자 회원가입", description = "새로운 관리자 계정을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패 또는 중복된 로그인 ID")
    })
    @PostMapping("/admin/sign-up")
    SuccessResponse<SignUpResponse> signUp(
            @RequestBody @Valid SignUpRequest request,
            @Parameter(hidden = true) HttpServletRequest httpRequest
    );

    @Operation(summary = "관리자 로그인", description = "관리자 로그인을 수행하고 세션을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "로그인 정보가 올바르지 않음")
    })
    @PostMapping("/admin/login")
    SuccessResponse<AdminLoginResponse> login(
            @RequestBody @Valid LoginRequest request,
            @Parameter(hidden = true) HttpServletRequest httpRequest
    );

    @Operation(summary = "관리자 로그아웃", description = "관리자 로그아웃을 수행하고 세션을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공", useReturnTypeSchema = true)
    })
    @PostMapping("/admin/logout")
    SuccessResponse<String> logout(
            @Parameter(hidden = true) HttpServletRequest httpRequest
    );

    @Operation(summary = "관리자 정보 조회", description = "현재 로그인한 관리자의 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "로그인되지 않은 상태")
    })
    @GetMapping("/admin/me")
    SuccessResponse<AdminLoginResponse> getAdminLoginStatus(
            @Parameter(hidden = true) @AdminAuthenticationPrincipal AdminSession adminSession
    );
}
