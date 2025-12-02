package feedzupzup.backend.qr.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import feedzupzup.backend.qr.domain.QR;
import feedzupzup.backend.qr.dto.response.QRDownloadUrlResponse;
import feedzupzup.backend.qr.dto.response.QRResponse;
import feedzupzup.backend.qr.repository.QRRepository;
import feedzupzup.backend.s3.service.S3PresignedDownloadService;
import feedzupzup.backend.s3.service.S3UploadService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class QRServiceTest extends ServiceIntegrationHelper {

    @Autowired
    private QRService qrService;

    @Autowired
    private QRRepository qrRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @MockitoBean
    private QRCodeGenerator qrCodeGenerator;

    @MockitoBean
    private S3UploadService s3UploadService;

    @MockitoBean
    private S3PresignedDownloadService s3PresignedDownloadService;

    @Test
    @DisplayName("유효한 조직 UUID로 QR을 성공적으로 조회한다")
    void getQR_success() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        final QR qr = new QR("https://example.com/qr-image.png", organization);
        qrRepository.save(qr);

        // when
        final QRResponse response = qrService.getQRCode(organization.getUuid());

        // then
        assertAll(
                () -> assertThat(response.imageUrl()).isEqualTo("https://example.com/qr-image.png"),
                () -> assertThat(response.siteUrl()).contains(organization.getUuid().toString())
        );
    }

    @Test
    @DisplayName("유효한 조직 UUID로 QR을 성공적으로 생성한다")
    void create_success() {
        // given
        final Organization organization = OrganizationFixture.createAllBlackBox();
        organizationRepository.save(organization);

        final byte[] mockQrImage = "mock-qr-image".getBytes();
        final String mockImageUrl = "https://s3.amazonaws.com/bucket/qr-image.png";

        when(qrCodeGenerator.generateQRCode(anyString())).thenReturn(mockQrImage);
        when(s3UploadService.uploadFile(anyString(), anyString(), anyString(), any(byte[].class)))
                .thenReturn(mockImageUrl);

        // when
        qrService.create(organization.getUuid());

        // then
        final QR savedQr = qrRepository.findByOrganizationId(organization.getId()).orElseThrow();
        assertAll(
                () -> assertThat(savedQr.getImageUrl()).isEqualTo(mockImageUrl),
                () -> assertThat(qrRepository.existsByOrganizationId(organization.getId())).isTrue()
        );
    }

    @Test
    @DisplayName("유효한 조직 UUID로 다운로드 URL을 성공적으로 생성한다")
    void getDownloadUrl_success() {
        // given
        final Organization organization = OrganizationFixture.createByName("테스트조직");
        organizationRepository.save(organization);

        final QR qr = new QR("https://s3.amazonaws.com/bucket/qr-image.png", organization);
        qrRepository.save(qr);

        final String mockDownloadUrl = "https://s3.amazonaws.com/bucket/presigned-download-url";
        when(s3PresignedDownloadService.generateDownloadUrlFromImageUrl(anyString(), anyString()))
                .thenReturn(mockDownloadUrl);

        // when
        final QRDownloadUrlResponse response = qrService.getDownloadUrl(organization.getUuid());

        // then
        assertThat(response.downloadUrl()).isEqualTo(mockDownloadUrl);
    }
}
