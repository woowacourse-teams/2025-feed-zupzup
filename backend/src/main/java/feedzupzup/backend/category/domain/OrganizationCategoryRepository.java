package feedzupzup.backend.category.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationCategoryRepository extends JpaRepository<OrganizationCategory, Long> {

}
