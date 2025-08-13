package feedzupzup.backend.notification.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationTokenRepository extends JpaRepository<NotificationToken, Long> {

    boolean existsByAdminId(Long adminId);
}
