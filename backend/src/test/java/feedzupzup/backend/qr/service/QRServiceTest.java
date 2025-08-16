package feedzupzup.backend.qr.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.global.exception.ResourceException.ResourceExistsException;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import feedzupzup.backend.qr.domain.QR;
import feedzupzup.backend.qr.dto.QRResponse;
import feedzupzup.backend.qr.repository.QRRepository;
import feedzupzup.backend.s3.service.S3UploadService;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    @Nested
    @DisplayName("QR 조회 테스트")
    class GetQRTest {

        @Test
        @DisplayName("유효한 조직 UUID로 QR을 성공적으로 조회한다")
        void getQR_success() {
            // given
            final Organization organization = OrganizationFixture.createAllBlackBox();
            organizationRepository.save(organization);

            final QR qr = new QR("https://example.com/qr-image.png", organization);
            qrRepository.save(qr);

            // when
            final QRResponse response = qrService.getQR(organization.getUuid().toString());

            // then
            assertAll(
                    () -> assertThat(response.imageUrl()).isEqualTo("https://example.com/qr-image.png"),
                    () -> assertThat(response.siteUrl()).isEqualTo(
                            "https://feedzupzup.com/dashboard?uuid=" + organization.getUuid())
            );
        }

        @Test
        @DisplayName("존재하지 않는 조직 UUID로 QR 조회 시 예외가 발생한다")
        void getQR_organization_not_found() {
            // given
            final UUID nonExistentUuid = UUID.randomUUID();

            // when & then
            assertThatThrownBy(() -> qrService.getQR(nonExistentUuid.toString()))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("해당 ID(id = " + nonExistentUuid + ")인 단체를 찾을 수 없습니다.");
        }

        @Test
        @DisplayName("조직은 존재하지만 QR이 없을 때 예외가 발생한다")
        void getQR_qr_not_found() {
            // given
            final Organization organization = OrganizationFixture.createAllBlackBox();
            organizationRepository.save(organization);

            // when & then
            assertThatThrownBy(() -> qrService.getQR(organization.getUuid().toString()))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("해당 ID(id = " + organization.getUuid() + ")인 단체의 QR 코드를 찾을 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("QR 생성 테스트")
    class CreateQRTest {

        @Test
        @DisplayName("유효한 조직 UUID로 QR을 성공적으로 생성한다")
        void create_success() {
            // given
            final Organization organization = OrganizationFixture.createAllBlackBox();
            organizationRepository.save(organization);

            final byte[] mockQrImage = "mock-qr-image".getBytes();
            final String mockImageUrl = "https://s3.amazonaws.com/bucket/qr-image.png";

            when(qrCodeGenerator.generateQRCode(anyString())).thenReturn(mockQrImage);
            when(s3UploadService.uploadFile(eq("png"), eq("/organization_qr"), eq(organization.getUuid()),
                    eq(mockQrImage)))
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
        @DisplayName("존재하지 않는 조직 UUID로 QR 생성 시 예외가 발생한다")
        void create_organization_not_found() {
            // given
            final UUID nonExistentUuid = UUID.randomUUID();

            // when & then
            assertThatThrownBy(() -> qrService.create(nonExistentUuid))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("해당 ID(id = " + nonExistentUuid + ")인 단체를 찾을 수 없습니다.");
        }

        @Test
        @DisplayName("이미 QR이 존재하는 조직에 QR 생성 시 예외가 발생한다")
        void create_qr_already_exists() {
            // given
            final Organization organization = OrganizationFixture.createAllBlackBox();
            organizationRepository.save(organization);

            final QR existingQr = new QR("https://example.com/existing-qr.png", organization);
            qrRepository.save(existingQr);

            // when & then
            assertThatThrownBy(() -> qrService.create(organization.getUuid()))
                    .isInstanceOf(ResourceExistsException.class)
                    .hasMessageContaining("해당 ID(id = " + organization.getUuid() + ")인 단체의 QR 코드는 이미 존재합니다.");
        }
    }
}
