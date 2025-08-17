package feedzupzup.backend.organizer.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrganizerRepository extends JpaRepository<Organizer, Long> {

    List<Organizer> findByOrganizationId(Long organizationId);

    boolean existsOrganizerByAdmin_IdAndOrganization_Id(Long adminId, Long organizationId);
}
