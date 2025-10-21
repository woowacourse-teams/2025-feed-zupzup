package feedzupzup.backend.organization.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Optional<Organization> findByUuid(UUID uuid);

    @Query("""
                SELECT new feedzupzup.backend.organization.domain.AdminOrganizationInfo(
                    o.uuid,
                    o.name.value,
                    SUM(CASE WHEN f.status = feedzupzup.backend.feedback.domain.vo.ProcessStatus.CONFIRMED THEN 1L ELSE 0L END),
                    SUM(CASE WHEN f.status = feedzupzup.backend.feedback.domain.vo.ProcessStatus.WAITING THEN 1L ELSE 0L END),
                    MAX(f.postedAt.value)
                )
                FROM Admin a
                JOIN Organizer or ON a.id = or.admin.id
                JOIN Organization o ON o.id = or.organization.id
                LEFT JOIN Feedback f ON o.id = f.organization.id
                WHERE a.id = :adminId
                GROUP BY o.id
                ORDER BY MAX(f.postedAt.value) DESC
            """)
    List<AdminOrganizationInfo> getAdminOrganizationInfos(Long adminId);

    boolean existsOrganizationByUuid(UUID uuid);
}
