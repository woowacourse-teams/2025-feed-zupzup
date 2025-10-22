package feedzupzup.backend.admin.domain;

import feedzupzup.backend.admin.domain.vo.LoginId;
import java.util.List;
import java.util.UUID;
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

    @Query("""
      SELECT EXISTS (
        SELECT 1
        FROM Admin admin
        INNER JOIN Organizer orger ON orger.admin.id = :adminId
        INNER JOIN Organization org ON orger.organization.id = org.id
        WHERE org.uuid = :organizationUuid
      )
      """)
    boolean existsByOrganizationUuid(Long adminId, UUID organizationUuid);
}
