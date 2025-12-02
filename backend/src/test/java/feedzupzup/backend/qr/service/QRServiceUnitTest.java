package feedzupzup.backend.qr.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import feedzupzup.backend.global.exception.ResourceException.ResourceExistsException;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import feedzupzup.backend.qr.repository.QRRepository;
import feedzupzup.backend.s3.service.S3PresignedDownloadService;
import feedzupzup.backend.s3.service.S3UploadService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QRServiceUnitTest {

    @InjectMocks
    private QRService qrService;

    @Mock
    private QRRepository qrRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private QRCodeGenerator qrCodeGenerator;

    @Mock
    private S3UploadService s3UploadService;

    @Mock
    private S3PresignedDownloadService s3PresignedDownloadService;

    @Nested
    @DisplayName("QR 조회 예외 테스트")
    class GetQRExceptionTest {

        @Test
        @DisplayName("존재하지 않는 조직 UUID로 QR 조회 시 예외가 발생한다")
        void getQR_organization_not_found() {
            // given
            UUID nonExistentUuid = UUID.randomUUID();

            given(organizationRepository.findByUuid(nonExistentUuid)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> qrService.getQRCode(nonExistentUuid))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("해당 ID(id = " + nonExistentUuid + ")인 단체를 찾을 수 없습니다.");
        }

        @Test
        @DisplayName("조직은 존재하지만 QR이 없을 때 예외가 발생한다")
        void getQR_qr_not_found() {
            // given
            Organization organization = OrganizationFixture.createAllBlackBox();
            UUID organizationUuid = organization.getUuid();

            given(organizationRepository.findByUuid(organizationUuid)).willReturn(Optional.of(organization));
            given(qrRepository.findByOrganizationId(organization.getId())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> qrService.getQRCode(organizationUuid))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("해당 ID(id = " + organizationUuid + ")인 단체의 QR 코드를 찾을 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("QR 생성 예외 테스트")
    class CreateQRExceptionTest {

        @Test
        @DisplayName("존재하지 않는 조직 UUID로 QR 생성 시 예외가 발생한다")
        void create_organization_not_found() {
            // given
            UUID nonExistentUuid = UUID.randomUUID();

            given(organizationRepository.findByUuid(nonExistentUuid)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> qrService.create(nonExistentUuid))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("해당 ID(id = " + nonExistentUuid + ")인 단체를 찾을 수 없습니다.");
        }

        @Test
        @DisplayName("이미 QR이 존재하는 조직에 QR 생성 시 예외가 발생한다")
        void create_qr_already_exists() {
            // given
            Organization organization = OrganizationFixture.createAllBlackBox();
            UUID organizationUuid = organization.getUuid();

            given(organizationRepository.findByUuid(organizationUuid)).willReturn(Optional.of(organization));
            given(qrRepository.existsByOrganizationId(organization.getId())).willReturn(true);

            // when & then
            assertThatThrownBy(() -> qrService.create(organizationUuid))
                    .isInstanceOf(ResourceExistsException.class)
                    .hasMessageContaining("해당 ID(id = " + organizationUuid + ")인 단체의 QR 코드는 이미 존재합니다.");
        }
    }

    @Nested
    @DisplayName("QR 다운로드 URL 생성 예외 테스트")
    class GetDownloadUrlExceptionTest {

        @Test
        @DisplayName("존재하지 않는 조직 UUID로 다운로드 URL 생성 시 예외가 발생한다")
        void getDownloadUrl_organization_not_found() {
            // given
            UUID nonExistentUuid = UUID.randomUUID();

            given(organizationRepository.findByUuid(nonExistentUuid)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> qrService.getDownloadUrl(nonExistentUuid))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("해당 ID(id = " + nonExistentUuid + ")인 단체를 찾을 수 없습니다.");
        }

        @Test
        @DisplayName("조직은 존재하지만 QR이 없을 때 다운로드 URL 생성 시 예외가 발생한다")
        void getDownloadUrl_qr_not_found() {
            // given
            Organization organization = OrganizationFixture.createAllBlackBox();
            UUID organizationUuid = organization.getUuid();

            given(organizationRepository.findByUuid(organizationUuid)).willReturn(Optional.of(organization));
            given(qrRepository.findByOrganizationId(organization.getId())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> qrService.getDownloadUrl(organizationUuid))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("해당 ID(id = " + organizationUuid + ")인 단체의 QR 코드를 찾을 수 없습니다.");
        }
    }
}
