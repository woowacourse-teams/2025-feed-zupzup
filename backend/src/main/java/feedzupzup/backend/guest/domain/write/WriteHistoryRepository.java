package feedzupzup.backend.guest.domain.write;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WriteHistoryRepository extends JpaRepository<WriteHistory, Long> {

    @Query("""
            SELECT DISTINCT wh
            FROM WriteHistory wh
            JOIN FETCH wh.guest g
            JOIN FETCH wh.feedback fb
            JOIN FETCH fb.organization o
            WHERE g.id = :guestId
            AND o.uuid = :organizationUuid
            """)
    List<WriteHistory> findWriteHistoriesBy(Long guestId, UUID organizationUuid);

}
