package feedzupzup.backend.guest.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.global.util.CurrentDateTime;
import feedzupzup.backend.guest.domain.guest.Guest;
import feedzupzup.backend.guest.domain.guest.GuestActiveTracker;
import feedzupzup.backend.guest.domain.guest.GuestRepository;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class GuestSchedulerTest extends ServiceIntegrationHelper {

    @Autowired
    private GuestScheduler guestScheduler;

    @Autowired
    private GuestActiveTracker guestActiveTracker;

    @Autowired
    private GuestRepository guestRepository;

    private Guest guest1;
    private Guest guest2;

    @BeforeEach
    void init() {
        guestActiveTracker.clear();
        guest1 = guestRepository.save(new Guest(UUID.randomUUID(), CurrentDateTime.create()));
        guest2 = guestRepository.save(new Guest(UUID.randomUUID(), CurrentDateTime.create()));
    }


    @Nested
    @DisplayName("스케줄러 실행 테스트")
    class SyncDailyActivityTest {

        @Test
        @DisplayName("활동한 게스트가 있을 때 트래커가 초기화된다")
        void syncDailyActivity_withActiveGuests_success() {
            // given
            guestActiveTracker.trackActivity(guest1.getGuestUuid());
            guestActiveTracker.trackActivity(guest2.getGuestUuid());
            assertThat(guestActiveTracker.getTodayActiveGuests()).hasSize(2);

            // when
            guestScheduler.updateDailyActiveGuestConnectedTime();

            // then
            assertThat(guestActiveTracker.getTodayActiveGuests()).isEmpty();
        }

        @Test
        @DisplayName("같은 게스트가 여러 번 활동해도 한 번만 기록된다")
        void syncDailyActivity_withDuplicateActivity_trackOnce() {
            // given
            guestActiveTracker.trackActivity(guest1.getGuestUuid());
            guestActiveTracker.trackActivity(guest1.getGuestUuid());

            // when
            final Set<UUID> activeGuests = guestActiveTracker.getTodayActiveGuests();

            // then
            assertThat(activeGuests).hasSize(1);
        }

        @Test
        @DisplayName("스케줄러 실행 후 트래커가 초기화된다")
        void syncDailyActivity_clearsTracker() {
            // given
            guestActiveTracker.trackActivity(guest1.getGuestUuid());
            assertThat(guestActiveTracker.getTodayActiveGuests()).hasSize(1);

            // when
            guestScheduler.updateDailyActiveGuestConnectedTime();

            // then
            assertThat(guestActiveTracker.getTodayActiveGuests()).isEmpty();
        }
    }
}
