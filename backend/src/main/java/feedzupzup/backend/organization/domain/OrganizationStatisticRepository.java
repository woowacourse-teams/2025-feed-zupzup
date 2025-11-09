package feedzupzup.backend.organization.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationStatisticRepository extends JpaRepository<OrganizationStatistic, Long> {

    OrganizationStatistic findByOrganizationId(Long organizationId);

    void deleteByOrganizationId(Long organizationId);

    void deleteAllByOrganizationIdIn(List<Long> organizationIds);
}
