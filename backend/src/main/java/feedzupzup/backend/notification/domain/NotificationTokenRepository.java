package feedzupzup.backend.notification.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationTokenRepository extends JpaRepository<NotificationToken, Long> {

    Optional<NotificationToken> findByAdmin_Id(Long adminId);
    
    void deleteAllByAdmin_IdIn(List<Long> adminIds);

    void deleteAllByAdmin_Id(Long adminId);
}
