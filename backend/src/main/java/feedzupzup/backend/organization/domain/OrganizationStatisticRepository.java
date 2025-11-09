package feedzupzup.backend.organization.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationStatisticRepository extends JpaRepository<OrganizationStatistic, Long> {

    OrganizationStatistic findByOrganizationId(Long organizationId);
}
