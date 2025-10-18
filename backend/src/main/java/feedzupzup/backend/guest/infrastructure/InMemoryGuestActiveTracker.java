package feedzupzup.backend.guest.infrastructure;

import feedzupzup.backend.guest.domain.guest.GuestActiveTracker;
import java.util.Collections;
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
        return Collections.unmodifiableSet(todayActiveGuests);
    }

    @Override
    public void clear() {
        todayActiveGuests.clear();
    }
}
