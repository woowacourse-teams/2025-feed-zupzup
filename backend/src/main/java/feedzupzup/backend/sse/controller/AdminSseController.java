package feedzupzup.backend.sse.controller;

import feedzupzup.backend.admin.dto.AdminSession;
import feedzupzup.backend.sse.api.AdminSseApi;
import feedzupzup.backend.sse.domain.ConnectionType;
import feedzupzup.backend.sse.service.SseService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class AdminSseController implements AdminSseApi {

    private final SseService sseService;

    @Override
    public SseEmitter subscribeAdmin(final UUID organizationUuid, final AdminSession adminSession) {
        return sseService.createEmitter(
                organizationUuid,
                adminSession.adminId().toString(),
                ConnectionType.ADMIN
        );
    }
}
