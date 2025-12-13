package feedzupzup.backend.category.domain;

import static org.assertj.core.api.Assertions.assertThat;

import feedzupzup.backend.category.fixture.OrganizationCategoryFixture;
import feedzupzup.backend.config.RepositoryHelper;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class OrganizationCategoryTest extends RepositoryHelper {

    @Autowired
    private OrganizationCategoryRepository organizationCategoryRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private EntityManager entityManager;

    @DisplayName("OrganizationCategory는 삭제시 soft delete가 적용된다.")
    @Test
    void organization_category_soft_delete() {
        // given
        Organization organization = OrganizationFixture.createAllBlackBox();
        Organization savedOrganization = organizationRepository.save(organization);
        OrganizationCategory organizationCategory = OrganizationCategoryFixture.createOrganizationCategory(
                savedOrganization);
        OrganizationCategory savedOrganizationCategory = organizationCategoryRepository.save(organizationCategory);

        // when
        organizationCategoryRepository.delete(savedOrganizationCategory);

        // then
        Optional<OrganizationCategory> nullable = organizationCategoryRepository.findById(savedOrganizationCategory.getId());
        assertThat(nullable).isEmpty();

        Object deletedAt = entityManager.createNativeQuery(
                        "SELECT deleted_at FROM organization_category WHERE id = ?")
                .setParameter(1, savedOrganizationCategory.getId())
                .getSingleResult();
        assertThat(deletedAt).isNotNull();
    }

}
