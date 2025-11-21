package feedzupzup.backend.global.domain;

import feedzupzup.backend.guest.domain.guest.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// Guest는 단순한 껍데기 입니다. JPA 문법 상 객체가 필요해, 임의로 Guest로 설정했습니다.
public interface LockRepository extends JpaRepository<Guest, Long> {

    @Query(value = "SELECT GET_LOCK(:key, :timeoutSeconds)", nativeQuery = true)
    Integer getLock(@Param("key") String key, @Param("timeoutSeconds") int timeoutSeconds);

    @Query(value = "SELECT RELEASE_LOCK(:key)", nativeQuery = true)
    Integer releaseLock(@Param("key") String key);

}
