package feedzupzup.backend.category.domain;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationCategoryRepository extends JpaRepository<OrganizationCategory, Long> {

    void deleteAllByOrganizationIdIn(Collection<Long> organizationIds);
    
    void deleteByOrganizationId(Long organizationId);
}
