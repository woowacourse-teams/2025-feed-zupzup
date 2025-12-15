package feedzupzup.backend.organization.controller;

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
import feedzupzup.backend.global.exception.GlobalExceptionHandler;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.organization.application.OrganizationStatisticService;
import feedzupzup.backend.organization.application.UserOrganizationService;
import feedzupzup.backend.organization.dto.request.CheeringRequest;
import feedzupzup.backend.organization.dto.response.CheeringResponse;
import feedzupzup.backend.organization.dto.response.UserOrganizationResponse;
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
class UserOrganizationControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private UserOrganizationService userOrganizationService;

    @Mock
    private OrganizationStatisticService organizationStatisticService;

    @InjectMocks
    private UserOrganizationController userOrganizationController;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(userOrganizationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("단체 ID로 단체 이름을 성공적으로 조회한다")
    void get_organization_name_success() throws Exception {
        // given
        UUID organizationUuid = UUID.randomUUID();
        String organizationName = "우아한테크코스";
        long cheeringCount = 100L;
        java.util.List<String> categories = java.util.List.of("건의", "신고");

        UserOrganizationResponse response = new UserOrganizationResponse(organizationName, cheeringCount, categories);

        given(userOrganizationService.getOrganizationByUuid(organizationUuid))
                .willReturn(response);

        // when & then
        mockMvc.perform(get("/organizations/{organizationUuid}", organizationUuid))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.organizationName").value(organizationName))
                .andExpect(jsonPath("$.data.totalCheeringCount").value(cheeringCount));

        verify(userOrganizationService).getOrganizationByUuid(organizationUuid);
    }

    @Test
    @DisplayName("존재하지 않는 단체 ID로 조회 시 404 에러를 반환한다")
    void get_organization_name_not_found() throws Exception {
        // given
        UUID nonExistentOrganizationUuid = UUID.randomUUID();

        given(userOrganizationService.getOrganizationByUuid(nonExistentOrganizationUuid))
                .willThrow(new ResourceNotFoundException("단체를 찾을 수 없습니다."));

        // when & then
        mockMvc.perform(get("/organizations/{organizationUuid}", nonExistentOrganizationUuid))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("G01"));

        verify(userOrganizationService).getOrganizationByUuid(nonExistentOrganizationUuid);
    }

    @Test
    @DisplayName("요청한 응원수만큼 해당 단체 id로 조회된 단체의 총 응원수가 증가한다")
    void cheer_organization_by_id() throws Exception {
        // given
        UUID organizationUuid = UUID.randomUUID();
        CheeringRequest request = new CheeringRequest(100);
        CheeringResponse response = new CheeringResponse(100L);

        given(userOrganizationService.cheer(any(CheeringRequest.class), eq(organizationUuid)))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/organizations/{organizationUuid}/cheer", organizationUuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.cheeringTotalCount").value(100));

        verify(userOrganizationService).cheer(any(CheeringRequest.class), eq(organizationUuid));
    }
}