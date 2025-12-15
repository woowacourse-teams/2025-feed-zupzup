package feedzupzup.backend.guest.domain.guest;

import static org.assertj.core.api.Assertions.assertThat;

import feedzupzup.backend.config.RepositoryHelper;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class GuestTest extends RepositoryHelper {

    @Autowired
    private GuestRepository guestRepository;

    @DisplayName("Guest 생성 시 guestUuid가 null이면 예외가 발생한다")
    @Test
    void constructor_guestUuidNull_throwsException() {
        // given
        LocalDateTime connectedTime = LocalDateTime.now();

        // when & then
        assertThat(org.junit.jupiter.api.Assertions.assertThrows(
                NullPointerException.class,
                () -> new Guest(null, connectedTime)
        )).isNotNull();
    }

    @DisplayName("Guest 생성 시 connectedTime이 null이면 예외가 발생한다")
    @Test
    void constructor_connectedTimeNull_throwsException() {
        // given
        UUID guestUuid = UUID.randomUUID();

        // when & then
        assertThat(org.junit.jupiter.api.Assertions.assertThrows(
                NullPointerException.class,
                () -> new Guest(guestUuid, null)
        )).isNotNull();
    }

    @DisplayName("Guest 생성 시 모든 값이 정상이면 정상 생성된다")
    @Test
    void constructor_allValid_success() {
        // given
        UUID guestUuid = UUID.randomUUID();
        LocalDateTime connectedTime = LocalDateTime.now();

        // when
        Guest guest = new Guest(guestUuid, connectedTime);
        Guest saved = guestRepository.save(guest);

        // then
        assertThat(saved.getGuestUuid()).isEqualTo(guestUuid);
        assertThat(saved.getConnectedTime()).isEqualTo(connectedTime);
    }

    @DisplayName("Guest 생성 시 서로 다른 UUID로 생성 가능하다")
    @Test
    void constructor_differentUuids_success() {
        // given
        UUID guestUuid1 = UUID.randomUUID();
        UUID guestUuid2 = UUID.randomUUID();
        LocalDateTime connectedTime = LocalDateTime.now();

        // when
        Guest guest1 = new Guest(guestUuid1, connectedTime);
        Guest guest2 = new Guest(guestUuid2, connectedTime);
        Guest saved1 = guestRepository.save(guest1);
        Guest saved2 = guestRepository.save(guest2);

        // then
        assertThat(saved1.getGuestUuid()).isEqualTo(guestUuid1);
        assertThat(saved2.getGuestUuid()).isEqualTo(guestUuid2);
        assertThat(saved1.getGuestUuid()).isNotEqualTo(saved2.getGuestUuid());
    }

    @DisplayName("Guest 생성 시 서로 다른 connectedTime으로 생성 가능하다")
    @Test
    void constructor_differentConnectedTimes_success() {
        // given
        UUID guestUuid = UUID.randomUUID();
        LocalDateTime connectedTime1 = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime connectedTime2 = LocalDateTime.of(2024, 12, 31, 23, 59);

        // when
        Guest guest1 = new Guest(guestUuid, connectedTime1);
        Guest guest2 = new Guest(UUID.randomUUID(), connectedTime2);
        Guest saved1 = guestRepository.save(guest1);
        Guest saved2 = guestRepository.save(guest2);

        // then
        assertThat(saved1.getConnectedTime()).isEqualTo(connectedTime1);
        assertThat(saved2.getConnectedTime()).isEqualTo(connectedTime2);
        assertThat(saved1.getConnectedTime()).isNotEqualTo(saved2.getConnectedTime());
    }
}
