package feedzupzup.backend.auth.presentation.api;

import feedzupzup.backend.admin.dto.AdminSession;
import feedzupzup.backend.auth.presentation.annotation.AdminAuthenticationPrincipal;
import feedzupzup.backend.auth.dto.response.LoginResponse;
import feedzupzup.backend.auth.dto.request.LoginRequest;
import feedzupzup.backend.auth.dto.request.SignUpRequest;
import feedzupzup.backend.auth.dto.response.SignUpResponse;
import feedzupzup.backend.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "AdminAuthorization", description = "관리자 인증 관련 API")
public interface AdminAuthApi {

    @Operation(summary = "관리자 회원가입", description = "새로운 관리자 계정을 생성합니다. 성공 시 자동으로 로그인되어 JSESSIONID 쿠키가 설정됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공 (자동 로그인, JSESSIONID 쿠키 설정됨)", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패 또는 중복된 로그인 ID")
    })
    @PostMapping("/admin/sign-up")
    SuccessResponse<SignUpResponse> signUp(
            @RequestBody @Valid SignUpRequest request,
            @Parameter(hidden = true) HttpServletRequest httpRequest
    );

    @Operation(summary = "관리자 로그인", description = "관리자 로그인을 수행하고 세션을 생성합니다. 성공 시 JSESSIONID 쿠키가 설정되어 이후 관리자 API 호출에 사용됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공 (JSESSIONID 쿠키 설정됨)", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "로그인 정보가 올바르지 않음")
    })
    @PostMapping("/admin/login")
    SuccessResponse<LoginResponse> login(
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
    @SecurityRequirement(name = "SessionAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized")
    })
    @GetMapping("/admin/me")
    SuccessResponse<LoginResponse> getAdminLoginStatus(
            @Parameter(hidden = true) @AdminAuthenticationPrincipal AdminSession adminSession
    );
}
