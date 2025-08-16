package feedzupzup.backend.qr.service;

import feedzupzup.backend.global.exception.ResourceException.ResourceExistsException;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.organization.event.OrganizationCreatedEvent;
import feedzupzup.backend.qr.infrastructure.exception.QRGenerationException;
import feedzupzup.backend.s3.exception.S3UploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrganizationEventHandler {

    private final QRService qrService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleOrganizationCreated(final OrganizationCreatedEvent event) {
        try {
            qrService.create(event.organizationUuid());
        } catch (ResourceNotFoundException e) {
            log.error("단체를 찾을 수 없어 QR 코드 생성 실패: organizationUuid={}, message={}", 
                    event.organizationUuid(), e.getMessage(), e);
        } catch (ResourceExistsException e) {
            log.warn("QR 코드가 이미 존재함: organizationUuid={}, message={}", 
                    event.organizationUuid(), e.getMessage());
        } catch (QRGenerationException e) {
            log.error("QR 코드 생성 라이브러리 오류: organizationUuid={}, message={}", 
                    event.organizationUuid(), e.getMessage(), e);
        } catch (S3UploadException e) {
            log.error("S3 업로드 실패로 QR 코드 생성 실패: organizationUuid={}, message={}", 
                    event.organizationUuid(), e.getMessage(), e);
        } catch (Exception e) {
            log.error("예상치 못한 오류로 QR 코드 생성 실패: organizationUuid={}, message={}", 
                    event.organizationUuid(), e.getMessage(), e);
        }
    }
}
