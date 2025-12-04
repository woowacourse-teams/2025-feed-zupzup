package feedzupzup.backend.sse.service;

import feedzupzup.backend.sse.domain.ConnectionType;
import feedzupzup.backend.sse.infrastructure.SseEmitterRepository;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
@Slf4j
public class SseService {

    @Qualifier("inMemorySseEmitterRepository")
    private final SseEmitterRepository sseEmitterRepository;

    public SseEmitter createEmitter(
            final UUID organizationUuid,
            final String userId,
            final ConnectionType connectionType
    ) {
        // TODO: 무제한 타임아웃 -> 하트비트로 수정
        final SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        final String emitterId = generateEmitterId(organizationUuid, userId, connectionType);

        sseEmitterRepository.save(emitterId, emitter);
        log.info("SSE 연결 생성 - Type: {}, Emitter ID: {}", connectionType, emitterId);

        emitter.onCompletion(() -> {
            log.info("SSE 연결 정상 종료 - {}", emitterId);
            sseEmitterRepository.remove(emitterId);
        });

        emitter.onTimeout(() -> {
            log.warn("SSE 연결 타임아웃 - {}", emitterId);
            sseEmitterRepository.remove(emitterId);
        });

        emitter.onError((e) -> {
            log.error("SSE 연결 에러 - {}: {}", emitterId, e.getMessage());
            sseEmitterRepository.remove(emitterId);
        });

        return emitter;
    }

    public void sendFeedbackNotificationToOrganization(final UUID organizationUuid, final long totalFeedbackCount) {
        final Map<String, SseEmitter> sseEmitters = sseEmitterRepository.findAllByOrganizationUuid(
                organizationUuid);
        log.info("피드백 수 전송 시작 - Organization: {}", organizationUuid);

        if (sseEmitters.isEmpty()) {
            log.info("전송 대상 연결 없음 - Organization: {}", organizationUuid);
            return;
        }

        log.info("전송 대상 연결 수: {}", sseEmitters.size());
        int successCount = 0;
        int failCount = 0;

        for (Map.Entry<String, SseEmitter> entry : sseEmitters.entrySet()) {
            final String emitterId = entry.getKey();
            final SseEmitter emitter = entry.getValue();

            try {
                emitter.send(SseEmitter.event()
                        .name("feedback-total-count-notification")
                        .data(totalFeedbackCount));
                successCount ++;
            } catch (IOException e) {
                log.warn("피드백 수 전송 실패 - Emitter: {}, 원인: {}", emitterId, e.getMessage());
                sseEmitterRepository.remove(emitterId);
                failCount++;
            }
        }
        log.info("피드백 수 전송 완료 - Organization: {}, 성공: {}, 실패: {}",
                organizationUuid, successCount, failCount);
    }

    private String generateEmitterId(
            final UUID organizationUuid,
            final String userId,
            final ConnectionType connectionType
    ) {
        return organizationUuid + "_"
                + connectionType.getPrefix() + "_"
                + userId + "_"
                + System.currentTimeMillis();
    }
}
