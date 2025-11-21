package feedzupzup.backend.guest.infrastructure;

import feedzupzup.backend.guest.domain.guest.GuestActiveTracker;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class InMemoryGuestActiveTracker implements GuestActiveTracker {

    private final Set<UUID> todayActiveGuests = ConcurrentHashMap.newKeySet();

    @Override
    public void trackActivity(UUID guestId) {
        todayActiveGuests.add(guestId);
    }

    @Override
    public Set<UUID> getTodayActiveGuests() {
        return new HashSet<>(todayActiveGuests);
    }

    @Override
    public void removeAll(Set<UUID> processedGuests) {
        todayActiveGuests.removeAll(processedGuests);
    }

    @Override
    public void clear() {
        todayActiveGuests.clear();
    }
}
