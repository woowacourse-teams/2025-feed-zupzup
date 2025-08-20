package feedzupzup.backend.admin.domain;

import feedzupzup.backend.admin.domain.vo.LoginId;
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
      FROM Admin a
      INNER JOIN Organizer org ON a.id = org.admin.id
      INNER JOIN Organization o ON o.id = org.organization.id
      INNER JOIN Feedback f ON f.organization.id = o.id
      WHERE a.id = :adminId AND f.id = :feedbackId
    )
    """)
    boolean existsFeedbackId(Long adminId, Long feedbackId);
}
