package feedzupzup.backend.guest.domain.guest;

import java.util.Set;
import java.util.UUID;

public interface GuestActiveTracker {

    void trackActivity(UUID guestId);

    Set<UUID> getTodayActiveGuests();

    void removeAll(Set<UUID> guests);

    void clear();
}
