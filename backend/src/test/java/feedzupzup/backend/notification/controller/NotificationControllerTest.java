package feedzupzup.backend.notification.controller;

import static feedzupzup.backend.auth.presentation.constants.RequestAttribute.ADMIN_ID;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import feedzupzup.backend.auth.presentation.resolver.AdminSessionArgumentResolver;
import feedzupzup.backend.global.exception.GlobalExceptionHandler;
import feedzupzup.backend.notification.application.NotificationService;
import feedzupzup.backend.notification.dto.request.NotificationRequest;
import feedzupzup.backend.notification.dto.request.UpdateAlertsSettingRequest;
import feedzupzup.backend.notification.dto.response.AlertsSettingResponse;
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
class NotificationControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController)
                .setCustomArgumentResolvers(new AdminSessionArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("알림 토큰을 성공적으로 등록한다")
    void registerNotification_Success() throws Exception {
        // given
        Long adminId = 1L;
        NotificationRequest request = new NotificationRequest("fcm-token-12345");
        String requestBody = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(post("/admin/notifications/token")
                        .requestAttr(ADMIN_ID.getValue(), adminId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("CREATED"));

        verify(notificationService).registerToken(request, adminId);
    }

    @Test
    @DisplayName("알림 설정을 성공적으로 조회한다")
    void getAlertsSetting_Success() throws Exception {
        // given
        Long adminId = 1L;
        AlertsSettingResponse response = new AlertsSettingResponse(true);

        given(notificationService.getAlertsSetting(adminId))
                .willReturn(response);

        // when & then
        mockMvc.perform(get("/admin/notifications/settings")
                        .requestAttr(ADMIN_ID.getValue(), adminId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.alertsOn").value(true));

        verify(notificationService).getAlertsSetting(adminId);
    }

    @Test
    @DisplayName("알림 설정을 성공적으로 업데이트한다")
    void updateAlertsSetting_Success() throws Exception {
        // given
        Long adminId = 1L;
        UpdateAlertsSettingRequest request = new UpdateAlertsSettingRequest(false);
        String requestBody = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(patch("/admin/notifications/settings")
                        .requestAttr(ADMIN_ID.getValue(), adminId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("OK"));

        verify(notificationService).updateAlertsSetting(request, adminId);
    }
}