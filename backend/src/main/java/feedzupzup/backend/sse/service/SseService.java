package feedzupzup.backend.sse.service;

import feedzupzup.backend.sse.domain.ConnectionType;
import feedzupzup.backend.sse.domain.SseEmitterRepository;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@Slf4j
public class SseService {

    private final SseEmitterRepository sseEmitterRepository;

    public SseService(
            @Qualifier("inMemorySseEmitterRepository")
            final SseEmitterRepository sseEmitterRepository
    ) {
        this.sseEmitterRepository = sseEmitterRepository;
    }

    public SseEmitter createEmitter(
            final UUID organizationUuid,
            final String userId,
            final ConnectionType connectionType
    ) {
        final SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        final String emitterId = generateEmitterId(organizationUuid, userId, connectionType);

        sseEmitterRepository.save(emitterId, emitter);
        log.info("SSE 연결 생성 - Type: {}, Emitter ID: {}", connectionType, emitterId);

        emitter.onCompletion(() -> {
            log.info("SSE 연결 정상 종료 - {}", emitterId);
            sseEmitterRepository.remove(emitterId);
        });

        emitter.onError((e) -> {
            final String message = e.getMessage();
            if (message != null && message.contains("disconnected client")) {
                log.debug("SSE 연결 종료(Client Disconnect)");
            } else {
                log.error("SSE 연결 에러 발생 - ID: {}, 메시지: {}", emitterId, message, e);
            }
            sseEmitterRepository.remove(emitterId);
        });

        emitter.onTimeout(() -> {
            log.warn("SSE 연결 타임아웃 - {}", emitterId);
            sseEmitterRepository.remove(emitterId);
        });

        sendToClient(emitter, emitterId, "connect", "EventStream Created");

        return emitter;
    }

    public void sendFeedbackNotificationToOrganization(final Long organizationId, final long totalFeedbackCount) {
        final Map<String, SseEmitter> sseEmitters = sseEmitterRepository.findAllByOrganizationId(
                organizationId);
        log.info("피드백 수 전송 시작 - Organization: {}", organizationId);

        if (sseEmitters.isEmpty()) {
            log.info("전송 대상 연결 없음 - Organization: {}", organizationId);
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
                organizationId, successCount, failCount);
    }

    private void sendToClient(final SseEmitter emitter, final String id, final String eventName, final Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .name(eventName)
                    .data(data));
        } catch (IOException e) {
            sseEmitterRepository.remove(id);
            log.error("SSE 연결오류 발생", e);
        }
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
