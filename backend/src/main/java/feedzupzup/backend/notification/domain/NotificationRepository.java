package feedzupzup.backend.notification.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Optional<Notification> findByAdminIdAndValue(Long adminId, String value);

    List<Notification> findByAdminId(Long adminId);
    
    void deleteAllByAdmin_IdIn(List<Long> adminIds);

    void deleteAllByAdmin_Id(Long adminId);
    
    void deleteAllByValueIn(List<String> values);
}
