package feedzupzup.backend.notification.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationTokenRepository extends JpaRepository<NotificationToken, Long> {

    boolean existsByAdminId(Long adminId);
    
    Optional<NotificationToken> findByAdminId(Long adminId);
    
    void deleteAllByAdminIdIn(List<Long> adminIds);
}
