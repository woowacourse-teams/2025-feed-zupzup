package feedzupzup.backend.auth.controller;

import static feedzupzup.backend.auth.presentation.constants.RequestAttribute.ADMIN_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import feedzupzup.backend.admin.domain.exception.AdminException.InvalidAdminNameException;
import feedzupzup.backend.admin.domain.exception.AdminException.InvalidAdminPasswordException;
import feedzupzup.backend.admin.dto.AdminSession;
import feedzupzup.backend.auth.application.AuthService;
import feedzupzup.backend.auth.dto.request.LoginRequest;
import feedzupzup.backend.auth.dto.request.SignUpRequest;
import feedzupzup.backend.auth.dto.response.LoginResponse;
import feedzupzup.backend.auth.dto.response.SignUpResponse;
import feedzupzup.backend.auth.exception.AuthException.DuplicateLoginIdException;
import feedzupzup.backend.auth.exception.AuthException.InvalidPasswordException;
import feedzupzup.backend.auth.presentation.controller.AdminAuthController;
import feedzupzup.backend.auth.presentation.resolver.AdminSessionArgumentResolver;
import feedzupzup.backend.auth.presentation.session.HttpSessionManager;
import feedzupzup.backend.global.exception.GlobalExceptionHandler;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AdminAuthControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AuthService authService;

    @Mock
    private HttpSessionManager httpSessionManager;

    @InjectMocks
    private AdminAuthController adminAuthController;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(adminAuthController)
                .setCustomArgumentResolvers(new AdminSessionArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("정상적인 회원가입 요청시 회원가입이 성공한다")
    void signUp_success() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest("testId", "password123", "testName");
        SignUpResponse response = new SignUpResponse(1L, "testId", "testName");

        given(authService.signUp(any(SignUpRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/admin/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("CREATED"))
                .andExpect(jsonPath("$.data.adminId").value(1))
                .andExpect(jsonPath("$.data.loginId").value("testId"))
                .andExpect(jsonPath("$.data.adminName").value("testName"));

        verify(httpSessionManager).createAdminSession(any(HttpServletRequest.class), eq(1L));
    }

    @Test
    @DisplayName("중복된 로그인 ID로 회원가입시 400 에러가 발생한다")
    void signUp_duplicateLoginId() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest("testId", "password123", "testName");

        given(authService.signUp(any(SignUpRequest.class)))
                .willThrow(new DuplicateLoginIdException("이미 존재하는 로그인 ID입니다."));

        // when & then
        mockMvc.perform(post("/admin/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("A05"));
    }

    @Test
    @DisplayName("유효하지 않은 비밀번호 형식으로 회원가입시 400 에러가 발생한다")
    void signUp_invalidPasswordFormat() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest("testId", "password한글", "testName");

        given(authService.signUp(any(SignUpRequest.class)))
                .willThrow(new InvalidAdminPasswordException("유효하지 않은 비밀 번호"));

        // when & then
        mockMvc.perform(post("/admin/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("A02"));
    }

    @Test
    @DisplayName("짧은 비밀번호로 회원가입시 400 에러가 발생한다")
    void signUp_shortPassword() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest("testId", "test", "testName");

        given(authService.signUp(any(SignUpRequest.class)))
                .willThrow(new InvalidAdminPasswordException("짧은 비밀 번호"));

        // when & then
        mockMvc.perform(post("/admin/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("A02"));
    }

    @Test
    @DisplayName("유효하지 않은 로그인 ID 형식으로 회원가입시 400 에러가 발생한다")
    void signUp_invalidLoginIdFormat() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest("test@#", "password123", "testName");

        given(authService.signUp(any(SignUpRequest.class)))
                .willThrow(new feedzupzup.backend.admin.domain.exception.AdminException.InvalidAdminIdException("유효하지 않은 로그인 ID"));

        // when & then
        mockMvc.perform(post("/admin/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("A01"));
    }

    @Test
    @DisplayName("유효하지 않은 관리자 이름 형식으로 회원가입시 400 에러가 발생한다")
    void signUp_invalidAdminNameFormat() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest("testId", "password123", "test@#");

        given(authService.signUp(any(SignUpRequest.class)))
                .willThrow(new InvalidAdminNameException("유효하지 않은 관리자 이름"));

        // when & then
        mockMvc.perform(post("/admin/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("A03"));
    }

    @Test
    @DisplayName("정상적인 로그인 요청시 로그인이 성공한다")
    void login_success() throws Exception {
        // given
        LoginRequest request = new LoginRequest("testId", "password123");
        LoginResponse response = new LoginResponse("testId", "testName", 1L);

        given(authService.login(any(LoginRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.loginId").value("testId"))
                .andExpect(jsonPath("$.data.adminName").value("testName"))
                .andExpect(jsonPath("$.data.adminId").value(1));

        verify(httpSessionManager).createAdminSession(any(HttpServletRequest.class), eq(1L));
    }

    @Test
    @DisplayName("존재하지 않는 로그인 ID로 로그인시 404 에러가 발생한다")
    void login_loginIdNotFound() throws Exception {
        // given
        LoginRequest request = new LoginRequest("noExistId", "password");

        given(authService.login(any(LoginRequest.class)))
                .willThrow(new ResourceNotFoundException("로그인 정보가 올바르지 않습니다"));

        // when & then
        mockMvc.perform(post("/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("G01"));
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 400 에러가 발생한다")
    void login_invalidPassword() throws Exception {
        // given
        LoginRequest request = new LoginRequest("testId", "wrongPassword123");

        given(authService.login(any(LoginRequest.class)))
                .willThrow(new InvalidPasswordException("로그인 정보가 올바르지 않습니다"));

        // when & then
        mockMvc.perform(post("/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("A06"));
    }

    @Test
    @DisplayName("로그인 후 로그아웃이 성공한다")
    void logout_success() throws Exception {
        // given
        Long adminId = 1L;
        AdminSession adminSession = new AdminSession(adminId);

        given(httpSessionManager.getAdminSession(any(HttpServletRequest.class)))
                .willReturn(adminSession);

        // when & then
        mockMvc.perform(post("/admin/logout"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").value("로그아웃이 완료되었습니다."));

        verify(authService).logout(eq(adminSession));
        verify(httpSessionManager).removeAdminSession(any(HttpServletRequest.class));
    }

    @Test
    @DisplayName("로그인 후 관리자 정보 조회가 성공한다")
    void getAdminInfo_success() throws Exception {
        // given
        Long adminId = 1L;
        LoginResponse response = new LoginResponse("testId", "testName", 1L);

        given(authService.getAdminLoginInfo(any(AdminSession.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(get("/admin/me")
                        .requestAttr(ADMIN_ID.getValue(), adminId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.loginId").value("testId"))
                .andExpect(jsonPath("$.data.adminName").value("testName"))
                .andExpect(jsonPath("$.data.adminId").value(1));
    }
}
