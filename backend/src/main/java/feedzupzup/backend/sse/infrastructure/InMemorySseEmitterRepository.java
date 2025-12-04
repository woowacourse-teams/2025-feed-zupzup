package feedzupzup.backend.sse.infrastructure;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class InMemorySseEmitterRepository implements SseEmitterRepository {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Override
    public void save(final String id, final SseEmitter emitter) {
        emitters.put(id, emitter);
    }

    @Override
    public void remove(final String id) {
        emitters.remove(id);
    }

    @Override
    public Map<String, SseEmitter> findAllByOrganizationUuid(final String organizationUuid) {
        return Map.of();
    }

    @Override
    public Map<String, SseEmitter> findAllByGuestUuid(final String organizationUuid,
            final String guestUuid) {
        return Map.of();
    }

    @Override
    public Map<String, SseEmitter> findAll() {
        return Map.of();
    }

    @Override
    public int count() {
        return emitters.size();
    }
}
