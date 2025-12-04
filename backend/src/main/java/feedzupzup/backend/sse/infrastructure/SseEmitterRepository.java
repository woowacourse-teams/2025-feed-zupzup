package feedzupzup.backend.sse.infrastructure;


import java.util.Map;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseEmitterRepository {

    void save(String id, SseEmitter emitter);

    void remove(String id);

    Map<String, SseEmitter> findAllByOrganizationUuid(String organizationUuid);

    Map<String, SseEmitter> findAllByGuestUuid(String organizationUuid, String guestUuid);

    Map<String, SseEmitter> findAll();

    int count();
}
