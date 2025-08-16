package feedzupzup.backend.qr.service;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import feedzupzup.backend.global.exception.ResourceException.ResourceExistsException;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.organization.event.OrganizationCreatedEvent;
import feedzupzup.backend.qr.infrastructure.exception.QRGenerationException;
import feedzupzup.backend.s3.exception.S3UploadException;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrganizationEventHandlerTest {

    @Mock
    private QRService qrService;

    @InjectMocks
    private OrganizationEventHandler organizationEventHandler;

    @Test
    @DisplayName("단체 생성 이벤트 처리 시 QR 서비스를 호출한다")
    void handleOrganizationCreated() {
        final UUID organizationUuid = UUID.randomUUID();
        final OrganizationCreatedEvent event = new OrganizationCreatedEvent(organizationUuid);

        organizationEventHandler.handleOrganizationCreated(event);

        verify(qrService).create(organizationUuid);
    }

    @Test
    @DisplayName("S3UploadException 발생 시 예외를 처리한다")
    void handleOrganizationCreated_S3UploadException_HandlesException() {
        // given
        final UUID organizationUuid = UUID.randomUUID();
        final OrganizationCreatedEvent event = new OrganizationCreatedEvent(organizationUuid);
        doThrow(new S3UploadException("S3 업로드 실패"))
                .when(qrService).create(organizationUuid);

        // when & then
        organizationEventHandler.handleOrganizationCreated(event);

        verify(qrService).create(organizationUuid);
    }

    @Test
    @DisplayName("ResourceNotFoundException 발생 시 예외를 처리한다")
    void handleOrganizationCreated_ResourceNotFoundException_HandlesException() {
        // given
        final UUID organizationUuid = UUID.randomUUID();
        final OrganizationCreatedEvent event = new OrganizationCreatedEvent(organizationUuid);
        doThrow(new ResourceNotFoundException("단체를 찾을 수 없습니다"))
                .when(qrService).create(organizationUuid);

        // when & then
        organizationEventHandler.handleOrganizationCreated(event);

        verify(qrService).create(organizationUuid);
    }

    @Test
    @DisplayName("ResourceExistsException 발생 시 예외를 처리한다")
    void handleOrganizationCreated_ResourceExistsException_HandlesException() {
        // given
        final UUID organizationUuid = UUID.randomUUID();
        final OrganizationCreatedEvent event = new OrganizationCreatedEvent(organizationUuid);
        doThrow(new ResourceExistsException("QR 코드가 이미 존재합니다"))
                .when(qrService).create(organizationUuid);

        // when & then
        organizationEventHandler.handleOrganizationCreated(event);

        verify(qrService).create(organizationUuid);
    }

    @Test
    @DisplayName("QRGenerationException 발생 시 예외를 처리한다")
    void handleOrganizationCreated_QRGenerationException_HandlesException() {
        // given
        final UUID organizationUuid = UUID.randomUUID();
        final OrganizationCreatedEvent event = new OrganizationCreatedEvent(organizationUuid);
        doThrow(new QRGenerationException("QR 코드 생성 실패"))
                .when(qrService).create(organizationUuid);

        // when & then
        organizationEventHandler.handleOrganizationCreated(event);

        verify(qrService).create(organizationUuid);
    }

    @Test
    @DisplayName("일반 Exception 발생 시 예외를 처리한다")
    void handleOrganizationCreated_GeneralException_HandlesException() {
        // given
        final UUID organizationUuid = UUID.randomUUID();
        final OrganizationCreatedEvent event = new OrganizationCreatedEvent(organizationUuid);
        doThrow(new RuntimeException("일반 예외"))
                .when(qrService).create(organizationUuid);

        // when & then
        organizationEventHandler.handleOrganizationCreated(event);

        verify(qrService).create(organizationUuid);
    }
}
