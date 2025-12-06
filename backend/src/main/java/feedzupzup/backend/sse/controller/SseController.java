package feedzupzup.backend.sse.controller;

import feedzupzup.backend.guest.dto.GuestInfo;
import feedzupzup.backend.sse.api.SseApi;
import feedzupzup.backend.sse.domain.ConnectionType;
import feedzupzup.backend.sse.service.SseService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class SseController implements SseApi {

    private final SseService sseService;

    @Override
    public SseEmitter subscribe(final UUID organizationUuid, final GuestInfo guestInfo) {
        return sseService.createEmitter(
                organizationUuid,
                guestInfo.guestUuid().toString(),
                ConnectionType.GUEST
        );
    }
}
