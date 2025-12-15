package feedzupzup.backend.organization.controller;

import static feedzupzup.backend.auth.presentation.constants.RequestAttribute.ADMIN_ID;
import static feedzupzup.backend.auth.presentation.constants.RequestAttribute.ORGANIZATION_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import feedzupzup.backend.auth.presentation.resolver.AdminSessionArgumentResolver;
import feedzupzup.backend.auth.presentation.resolver.OrganizerArgumentResolver;
import feedzupzup.backend.global.exception.GlobalExceptionHandler;
import feedzupzup.backend.organization.application.AdminOrganizationService;
import feedzupzup.backend.organization.dto.request.CreateOrganizationRequest;
import feedzupzup.backend.organization.dto.request.UpdateOrganizationRequest;
import feedzupzup.backend.organization.dto.response.AdminCreateOrganizationResponse;
import feedzupzup.backend.organization.dto.response.AdminInquireOrganizationResponse;
import feedzupzup.backend.organization.dto.response.AdminUpdateOrganizationResponse;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
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
class AdminOrganizationControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AdminOrganizationService adminOrganizationService;

    @InjectMocks
    private AdminOrganizationController adminOrganizationController;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(adminOrganizationController)
                .setCustomArgumentResolvers(
                        new AdminSessionArgumentResolver(),
                        new OrganizerArgumentResolver()
                )
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("정상적인 조직 생성 요청시 조직 생성이 성공한다")
    void createOrganization_Success() throws Exception {
        // given
        Long adminId = 1L;
        CreateOrganizationRequest request = new CreateOrganizationRequest("우아한테크코스", List.of("신고", "건의"));
        UUID organizationUuid = UUID.randomUUID();
        AdminCreateOrganizationResponse response = new AdminCreateOrganizationResponse(organizationUuid.toString());

        given(adminOrganizationService.createOrganization(any(CreateOrganizationRequest.class), eq(adminId)))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/admin/organizations")
                        .requestAttr(ADMIN_ID.getValue(), adminId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("CREATED"))
                .andExpect(jsonPath("$.data.organizationUuid").value(organizationUuid.toString()));

        verify(adminOrganizationService).createOrganization(any(CreateOrganizationRequest.class), eq(adminId));
    }

    @Test
    @DisplayName("정상적인 조직 조회 요청시 조직 조회가 성공한다")
    void getOrganizations_Success() throws Exception {
        // given
        Long adminId = 1L;
        UUID org1Uuid = UUID.randomUUID();
        UUID org2Uuid = UUID.randomUUID();

        List<AdminInquireOrganizationResponse> response = List.of(
                new AdminInquireOrganizationResponse(org1Uuid, "조직1", 5L, 3L, LocalDateTime.now()),
                new AdminInquireOrganizationResponse(org2Uuid, "조직2", 10L, 2L, LocalDateTime.now())
        );

        given(adminOrganizationService.getOrganizationsInfo(adminId))
                .willReturn(response);

        // when & then
        mockMvc.perform(get("/admin/organizations")
                        .requestAttr(ADMIN_ID.getValue(), adminId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].uuid").value(org1Uuid.toString()))
                .andExpect(jsonPath("$.data[1].uuid").value(org2Uuid.toString()));

        verify(adminOrganizationService).getOrganizationsInfo(adminId);
    }

    @Test
    @DisplayName("소속된 조직이 없는 경우, 빈 리스트를 반환한다")
    void getOrganizations_Success_Empty() throws Exception {
        // given
        Long adminId = 1L;
        List<AdminInquireOrganizationResponse> response = Collections.emptyList();

        given(adminOrganizationService.getOrganizationsInfo(adminId))
                .willReturn(response);

        // when & then
        mockMvc.perform(get("/admin/organizations")
                        .requestAttr(ADMIN_ID.getValue(), adminId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.length()").value(0));

        verify(adminOrganizationService).getOrganizationsInfo(adminId);
    }

    @Test
    @DisplayName("정상적인 조직 수정 요청시 조직 수정이 성공한다")
    void updateOrganization_Success() throws Exception {
        // given
        UUID organizationUuid = UUID.randomUUID();
        UpdateOrganizationRequest request = new UpdateOrganizationRequest("새로운 조직", List.of("신고", "건의", "기타"));
        AdminUpdateOrganizationResponse response = new AdminUpdateOrganizationResponse(organizationUuid.toString(), "새로운 조직", List.of("신고", "건의", "기타"));

        given(adminOrganizationService.updateOrganization(eq(organizationUuid), any(UpdateOrganizationRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(put("/admin/organizations/{organizationUuid}", organizationUuid)
                        .requestAttr(ORGANIZATION_ID.getValue(), organizationUuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.updateName").value("새로운 조직"))
                .andExpect(jsonPath("$.data.updateCategories.length()").value(3));

        verify(adminOrganizationService).updateOrganization(eq(organizationUuid), any(UpdateOrganizationRequest.class));
    }

    @Test
    @DisplayName("관리자가 자신이 소유한 조직을 성공적으로 삭제한다")
    void deleteOrganization_Success() throws Exception {
        // given
        UUID organizationUuid = UUID.randomUUID();

        doNothing().when(adminOrganizationService).deleteOrganization(organizationUuid);

        // when & then
        mockMvc.perform(delete("/admin/organizations/{organizationUuid}", organizationUuid)
                        .requestAttr(ORGANIZATION_ID.getValue(), organizationUuid))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.status").value(204))
                .andExpect(jsonPath("$.message").value("NO_CONTENT"));

        verify(adminOrganizationService).deleteOrganization(organizationUuid);
    }
}