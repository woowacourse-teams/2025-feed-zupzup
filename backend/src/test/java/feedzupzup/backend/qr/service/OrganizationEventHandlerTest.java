package feedzupzup.backend.qr.service;

import static org.mockito.Mockito.verify;

import feedzupzup.backend.organization.event.OrganizationCreatedEvent;
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
}
