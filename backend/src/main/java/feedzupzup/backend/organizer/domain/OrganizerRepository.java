package feedzupzup.backend.organizer.domain;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrganizerRepository extends JpaRepository<Organizer, Long> {

    @Query("SELECT o FROM Organizer o JOIN FETCH o.admin WHERE o.organization.id = :organizationId")
    List<Organizer> findByOrganizationId(@Param("organizationId") Long organizationId);

    boolean existsOrganizerByAdmin_IdAndOrganization_Id(Long adminId, Long organizationId);

    boolean existsOrganizerByAdmin_IdAndOrganization_Uuid(Long adminId, UUID organizationUuid);

}
