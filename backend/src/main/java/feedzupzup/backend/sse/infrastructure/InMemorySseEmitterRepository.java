package feedzupzup.backend.sse.infrastructure;

import feedzupzup.backend.sse.domain.SseEmitterRepository;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
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
    public Map<String, SseEmitter> findAllByOrganizationId(final Long organizationId) {
        final String prefix = organizationId.toString() + "_";

        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(prefix))
                .collect(Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue)
        );
    }

    @Override
    public Map<String, SseEmitter> findAll() {
        return Collections.unmodifiableMap(new HashMap<>(emitters));
    }

    @Override
    public int count() {
        return emitters.size();
    }
}
