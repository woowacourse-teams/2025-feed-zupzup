package feedzupzup.backend.sse.infrastructure;

import feedzupzup.backend.sse.domain.SseEmitterRepository;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
@RequiredArgsConstructor
@Slf4j
public class SseHeartbeatScheduler {

    @Qualifier("inMemorySseEmitterRepository")
    private final SseEmitterRepository sseEmitterRepository;

    @Scheduled(cron = "0/10 * * * * *")
    public void sendHeartbeat() {
        final Map<String, SseEmitter> emitters = sseEmitterRepository.findAll();

        if (emitters.isEmpty()) {
            log.debug("하트비스 스케줄링 스킵");
            return;
        }

        log.info("Heartbeat 전송 - 대상: {}개", emitters.size());

        for (Map.Entry<String, SseEmitter> entry : emitters.entrySet()) {
            final String emitterId = entry.getKey();
            final SseEmitter emitter = entry.getValue();

            try {
                emitter.send(SseEmitter.event()
                        .name("heartbeat")
                        .data("ping"));
            } catch (IOException e) {
                log.info("좀비 연결 제거 - ID : {} " , emitterId);
                sseEmitterRepository.remove(emitterId);
            }
        }

        log.debug("Heartbeat 완료 ");
    }
}
