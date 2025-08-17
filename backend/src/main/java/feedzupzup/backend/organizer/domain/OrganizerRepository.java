package feedzupzup.backend.organizer.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrganizerRepository extends JpaRepository<Organizer, Long> {

    List<Organizer> findByOrganizationId(Long organizationId);

    boolean existsOrganizerByAdminIdAndOrganizationId(Long adminId, Long organizationId);
}
