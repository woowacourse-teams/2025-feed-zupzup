package feedzupzup.backend.qr.service;

import feedzupzup.backend.global.exception.ResourceException.ResourceExistsException;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.qr.config.QRProperties;
import feedzupzup.backend.qr.domain.QR;
import feedzupzup.backend.qr.domain.SiteUrl;
import feedzupzup.backend.qr.dto.request.QRCodeUploadRequest;
import feedzupzup.backend.qr.dto.response.QRDownloadUrlResponse;
import feedzupzup.backend.qr.dto.response.QRResponse;
import feedzupzup.backend.qr.repository.QRRepository;
import feedzupzup.backend.s3.service.S3PresignedDownloadService;
import feedzupzup.backend.s3.service.S3UploadService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QRService {

    private final QRRepository qrRepository;
    private final OrganizationRepository organizationRepository;
    private final QRCodeGenerator qrCodeGenerator;
    private final S3UploadService s3UploadService;
    private final S3PresignedDownloadService s3PresignedDownloadService;
    private final SiteUrl siteUrl;
    private final QRProperties qrProperties;

    public QRResponse getQRCode(final UUID organizationUuid) {
        final Organization organization = getOrganization(organizationUuid);
        final QR qr = getQr(organization);

        final String siteUrl = buildSiteUrl(organizationUuid);

        return QRResponse.of(qr, siteUrl);
    }

    @Transactional
    public void create(final UUID organizationUuid) {
        final Organization organization = getOrganization(organizationUuid);

        if (qrRepository.existsByOrganizationId(organization.getId())) {
            throw new ResourceExistsException(
                    "해당 ID(id = " + organizationUuid + ")인 단체의 QR 코드는 이미 존재합니다.");
        }

        final String siteUrl = buildSiteUrl(organizationUuid);

        final byte[] qrCode = qrCodeGenerator.generateQRCode(siteUrl);
        final String imageUrl = s3UploadService.uploadFile(
                new QRCodeUploadRequest(
                        qrProperties.image().extension(),
                        "organization_qr",
                        organization.getUuid().toString(),
                        qrCode
                ));

        qrRepository.save(new QR(imageUrl, organization));
    }

    public QRDownloadUrlResponse getDownloadUrl(final UUID organizationUuid) {
        final Organization organization = getOrganization(organizationUuid);
        final QR qr = getQr(organization);

        final String qrPrefix = "QR.";
        final String downloadFileName = qrPrefix + qrProperties.image().extension().toLowerCase();
        final String presignedUrl = s3PresignedDownloadService.generateDownloadUrlFromImageUrl(
                qr.getImageUrl(), downloadFileName);

        return new QRDownloadUrlResponse(presignedUrl);
    }

    private Organization getOrganization(final UUID uuid) {
        return organizationRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("해당 ID(id = " + uuid + ")인 단체를 찾을 수 없습니다."));
    }

    private QR getQr(final Organization organization) {
        return qrRepository.findByOrganizationId(organization.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "해당 ID(id = " + organization.getUuid() + ")인 단체의 QR 코드를 찾을 수 없습니다."));
    }

    private String buildSiteUrl(final UUID organizationUuid) {
        final String paramKey = "uuid";
        return siteUrl.builder()
                .addParam(paramKey, organizationUuid.toString())
                .build();
    }
}
