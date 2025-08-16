package feedzupzup.backend.organization.domain;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Optional<Organization> findByUuid(UUID uuid);

    @Query("""
                SELECT new feedzupzup.backend.organization.domain.AdminOrganizationInfo(
                    o.name.value,
                    SUM(CASE WHEN f.status = feedzupzup.backend.feedback.domain.vo.ProcessStatus.WAITING THEN 1L ELSE 0L END),
                    MAX(f.postedAt.postedAt)
                )
                FROM Admin a
                JOIN Organizer or ON a.id = or.admin.id
                JOIN Organization o ON o.id = or.organization.id
                LEFT JOIN Feedback f ON o.id = f.organization.id
                WHERE a.id = :adminId
                GROUP BY o.id
                ORDER BY MAX(f.postedAt.postedAt) DESC
            """)
    List<AdminOrganizationInfo> getAdminOrganizationInfos(Long adminId);
}
