package feedzupzup.backend.sse.domain;


import java.util.Map;
import java.util.UUID;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseEmitterRepository {

    void save(String id, SseEmitter emitter);

    void remove(String id);

    Map<String, SseEmitter> findAllByOrganizationId(Long organizationId);

    Map<String, SseEmitter> findAll();
    int count();
}
