package feedzupzup.backend.guest.domain.guest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Long> {

    Optional<Guest> findByGuestUuid(UUID guestUuid);

    boolean existsByGuestUuid(UUID guestUuid);

    @Modifying
    @Query("UPDATE Guest g SET g.connectedTime = :time WHERE g.guestUuid IN :ids")
    void updateConnectedTimeForGuests(
            @Param("ids") Set<UUID> ids,
            @Param("time") LocalDateTime time
    );
}
