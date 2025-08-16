package feedzupzup.backend.qr.service;

import feedzupzup.backend.global.exception.ResourceException.ResourceExistsException;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.qr.domain.QR;
import feedzupzup.backend.qr.dto.QRResponse;
import feedzupzup.backend.qr.infrastructure.QRImageGenerator;
import feedzupzup.backend.qr.repository.QRRepository;
import feedzupzup.backend.s3.service.S3UploadService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QRService {

    private final QRRepository qrRepository;
    private final OrganizationRepository organizationRepository;
    private final QRImageGenerator qrImageGenerator;
    private final S3UploadService s3UploadService;

    @Value("${page.base-url}")
    private String baseUrl;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void create(final UUID organizationUuid) {
        final Organization organization = getOrganization(organizationUuid);

        if (qrRepository.existsByOrganizationId(organization.getId())) {
            throw new ResourceExistsException(
                    "해당 ID(id = " + organizationUuid + ")인 단체의 QR 코드는 이미 존재합니다.");
        }

        final byte[] qrImage = qrImageGenerator.generateQRImage(baseUrl + "?uuid=" + organizationUuid);
        final String imageUrl = s3UploadService.uploadFile(
                "png", "/organization_qr", organization.getUuid(), qrImage);

        qrRepository.save(new QR(imageUrl, organization));
    }

    private Organization getOrganization(final UUID uuid) {
        return organizationRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("해당 ID(id = " + uuid + ")인 단체를 찾을 수 없습니다."));
    }
}
