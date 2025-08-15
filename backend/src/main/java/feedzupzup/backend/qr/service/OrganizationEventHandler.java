package feedzupzup.backend.qr.service;

import feedzupzup.backend.organization.event.OrganizationCreatedEvent;
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
        qrService.create(event.organizationUuid());
    }
}
