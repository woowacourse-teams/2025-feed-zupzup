package feedzupzup.backend.sse.infrastructure;

import feedzupzup.backend.sse.domain.SseEmitterRepository;
import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
@Slf4j
public class SseHeartbeatScheduler {

    private final SseEmitterRepository sseEmitterRepository;

    public SseHeartbeatScheduler(
            @Qualifier("inMemorySseEmitterRepository")
            final SseEmitterRepository sseEmitterRepository
    ) {
        this.sseEmitterRepository = sseEmitterRepository;
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void sendHeartbeat() {
        final Map<String, SseEmitter> emitters = sseEmitterRepository.findAll();

        if (emitters.isEmpty()) {
            log.debug("하트비트 스케줄링 스킵");
            return;
        }

        log.info("Heartbeat 전송 - 대상: {}개", emitters.size());

        //TODO : 순차전송이 아닌, 비동기 전송을 고려해보자
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
