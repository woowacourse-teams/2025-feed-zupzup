package feedzupzup.backend.qr.controller;

import static feedzupzup.backend.auth.presentation.constants.RequestAttribute.ORGANIZATION_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import feedzupzup.backend.auth.presentation.resolver.OrganizerArgumentResolver;
import feedzupzup.backend.global.exception.GlobalExceptionHandler;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.qr.dto.response.QRDownloadUrlResponse;
import feedzupzup.backend.qr.dto.response.QRResponse;
import feedzupzup.backend.qr.service.QRService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class QRControllerTest {

    private MockMvc mockMvc;

    @Mock
    private QRService qrService;

    @InjectMocks
    private QRController qrController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(qrController)
                .setCustomArgumentResolvers(new OrganizerArgumentResolver())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    @DisplayName("QR 코드 조회 테스트")
    class GetQRTest {

        @Test
        @DisplayName("조직 UUID로 QR 코드를 성공적으로 조회한다")
        void getQR_success() throws Exception {
            // given
            UUID organizationUuid = UUID.randomUUID();
            String imageUrl = "https://example.com/qr-image.png";
            String siteUrl = "https://example.com/site/" + organizationUuid;

            QRResponse response = new QRResponse(imageUrl, siteUrl);

            given(qrService.getQRCode(any(UUID.class)))
                    .willReturn(response);

            // when & then
            mockMvc.perform(get("/admin/organizations/{organizationUuid}/qr-code", organizationUuid)
                            .requestAttr(ORGANIZATION_ID.getValue(), organizationUuid))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.message").value("OK"))
                    .andExpect(jsonPath("$.data.imageUrl").value(imageUrl))
                    .andExpect(jsonPath("$.data.siteUrl").value(siteUrl));

            verify(qrService).getQRCode(organizationUuid);
        }

        @Test
        @DisplayName("조직은 존재하지만 QR이 없을 때 404 에러를 반환한다")
        void getQR_qr_not_found() throws Exception {
            // given
            UUID organizationUuid = UUID.randomUUID();

            given(qrService.getQRCode(any(UUID.class)))
                    .willThrow(new ResourceNotFoundException("QR 코드를 찾을 수 없습니다."));

            // when & then
            mockMvc.perform(get("/admin/organizations/{organizationUuid}/qr-code", organizationUuid)
                            .requestAttr(ORGANIZATION_ID.getValue(), organizationUuid))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.code").value("G01"));

            verify(qrService).getQRCode(organizationUuid);
        }
    }

    @Nested
    @DisplayName("QR 다운로드 URL 조회 테스트")
    class GetQRDownloadUrlTest {

        @Test
        @DisplayName("QR 다운로드 URL을 성공적으로 조회한다")
        void getQRDownloadUrl_success() throws Exception {
            // given
            UUID organizationUuid = UUID.randomUUID();
            String downloadUrl = "https://s3.amazonaws.com/bucket/qr-code.png?presigned=true";

            QRDownloadUrlResponse response = new QRDownloadUrlResponse(downloadUrl);

            given(qrService.getDownloadUrl(any(UUID.class)))
                    .willReturn(response);

            // when & then
            mockMvc.perform(get("/admin/organizations/{organizationUuid}/qr-code/download-url", organizationUuid)
                            .requestAttr(ORGANIZATION_ID.getValue(), organizationUuid))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.message").value("OK"))
                    .andExpect(jsonPath("$.data.downloadUrl").value(downloadUrl));

            verify(qrService).getDownloadUrl(organizationUuid);
        }

        @Test
        @DisplayName("조직은 존재하지만 QR이 없을 때 다운로드 URL 조회 시 404 에러를 반환한다")
        void getQRDownloadUrl_qr_not_found() throws Exception {
            // given
            UUID organizationUuid = UUID.randomUUID();

            given(qrService.getDownloadUrl(any(UUID.class)))
                    .willThrow(new ResourceNotFoundException("QR 코드를 찾을 수 없습니다."));

            // when & then
            mockMvc.perform(get("/admin/organizations/{organizationUuid}/qr-code/download-url", organizationUuid)
                            .requestAttr(ORGANIZATION_ID.getValue(), organizationUuid))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.code").value("G01"));

            verify(qrService).getDownloadUrl(organizationUuid);
        }
    }
}