package feedzupzup.backend.admin.controller;

import static feedzupzup.backend.auth.presentation.constants.RequestAttribute.ADMIN_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import feedzupzup.backend.admin.application.AdminService;
import feedzupzup.backend.auth.presentation.resolver.AdminSessionArgumentResolver;
import feedzupzup.backend.auth.presentation.session.HttpSessionManager;
import feedzupzup.backend.global.exception.GlobalExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AdminService adminService;

    @Mock
    private HttpSessionManager httpSessionManager;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController)
                .setCustomArgumentResolvers(new AdminSessionArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("관리자가 회원탈퇴를 성공적으로 수행한다")
    void withdraw_success() throws Exception {
        // given
        Long adminId = 1L;

        // when & then
        mockMvc.perform(delete("/admin")
                        .requestAttr(ADMIN_ID.getValue(), adminId))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.status").value(204))
                .andExpect(jsonPath("$.message").value("NO_CONTENT"));

        verify(httpSessionManager).removeAdminSession(any(HttpServletRequest.class));
        verify(adminService).withdraw(adminId);
    }
}