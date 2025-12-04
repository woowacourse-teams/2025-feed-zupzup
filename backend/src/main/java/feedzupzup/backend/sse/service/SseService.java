package feedzupzup.backend.sse.service;

import feedzupzup.backend.sse.infrastructure.SseEmitterRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class SseService {

    @Qualifier("inMemorySseEmitterRepository")
    private final SseEmitterRepository sseEmitterRepository;

    public SseEmitter createEmitter(final UUID organizationUuid, final UUID guestUuid) {
        // TODO: 무제한 타임아웃 -> 하트비트로 수정
        final SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        final String emitterId = generateEmitterId(organizationUuid, guestUuid);

        sseEmitterRepository.save(emitterId, emitter);

        emitter.onCompletion(() -> {
            sseEmitterRepository.remove(emitterId);
        });

        emitter.onTimeout(() -> {
            sseEmitterRepository.remove(emitterId);
        });

        emitter.onError((e) -> {
            sseEmitterRepository.remove(emitterId);
        });

        return emitter;
    }

    private String generateEmitterId(final UUID organizationUuid, UUID guestUuid) {
        return organizationUuid + "_" + guestUuid + "_" + System.currentTimeMillis();
    }
}
