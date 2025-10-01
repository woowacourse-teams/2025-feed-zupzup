package feedzupzup.backend.admin.domain;

import feedzupzup.backend.admin.domain.vo.LoginId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByLoginId(final LoginId loginId);
    
    boolean existsByLoginId(final LoginId loginId);

    @Query("""
      SELECT EXISTS (
        SELECT 1
        FROM Organizer org
        INNER JOIN Feedback f ON f.organization.id = org.organization.id
        WHERE org.admin.id = :adminId AND f.id = :feedbackId
      )
      """)
    boolean existsFeedbackId(Long adminId, Long feedbackId);
    
    @Query("SELECT a.id FROM Admin a WHERE a.id IN :adminIds AND a.alertsOn = true")
    List<Long> findAlertsEnabledAdminIds(List<Long> adminIds);
}
