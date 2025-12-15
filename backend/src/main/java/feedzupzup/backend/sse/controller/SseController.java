package feedzupzup.backend.sse.controller;

import feedzupzup.backend.guest.dto.GuestInfo;
import feedzupzup.backend.sse.api.SseApi;
import feedzupzup.backend.sse.domain.ConnectionType;
import feedzupzup.backend.sse.service.SseService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class SseController implements SseApi {

    private final SseService sseService;

    @Override
    public ResponseEntity<SseEmitter> subscribe(final UUID organizationUuid, final GuestInfo guestInfo) {
        final SseEmitter emitter = sseService.createEmitter(
                organizationUuid,
                guestInfo.guestUuid().toString(),
                ConnectionType.GUEST
        );
        return ResponseEntity.ok()
                .header("X-Accel-Buffering", "no")
                .header("Cache-Control", "no-cache")
                .body(emitter);
    }
}
