package feedzupzup.backend.category.application;

import static feedzupzup.backend.category.domain.Category.SUGGESTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.category.domain.OrganizationCategoryRepository;
import feedzupzup.backend.category.fixture.OrganizationCategoryFixture;
import feedzupzup.backend.config.ServiceIntegrationHelper;
import feedzupzup.backend.organization.domain.Organization;
import feedzupzup.backend.organization.domain.OrganizationRepository;
import feedzupzup.backend.organization.fixture.OrganizationFixture;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class OrganizationCategoryServiceTest extends ServiceIntegrationHelper {

    @Autowired
    private OrganizationCategoryService organizationCategoryService;

    @Autowired
    private OrganizationCategoryRepository organizationCategoryRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Test
    @DisplayName("여러 단체 ID로 카테고리들을 일괄 삭제한다")
    void deleteAllByOrganizationIds_success() {
        // given
        Organization organization1 = OrganizationFixture.createAllBlackBox();
        Organization organization2 = OrganizationFixture.createAllBlackBox();
        Organization organization3 = OrganizationFixture.createAllBlackBox();
        
        organizationRepository.save(organization1);
        organizationRepository.save(organization2);
        organizationRepository.save(organization3);

        OrganizationCategory category1 = OrganizationCategoryFixture.createOrganizationCategory(organization1, SUGGESTION);
        OrganizationCategory category2 = OrganizationCategoryFixture.createOrganizationCategory(organization2, SUGGESTION);
        OrganizationCategory category3 = OrganizationCategoryFixture.createOrganizationCategory(organization3, SUGGESTION);
        
        organizationCategoryRepository.save(category1);
        organizationCategoryRepository.save(category2);
        organizationCategoryRepository.save(category3);

        List<Long> organizationIds = List.of(organization1.getId(), organization2.getId());

        // when
        organizationCategoryService.deleteAllByOrganizationIds(organizationIds);

        // then
        List<OrganizationCategory> remainingCategories = organizationCategoryRepository.findAll();
        assertAll(
                () -> assertThat(remainingCategories).hasSize(1),
                () -> assertThat(remainingCategories.get(0).getOrganization().getId()).isEqualTo(organization3.getId())
        );
    }

    @Test
    @DisplayName("카테고리들을 일괄 저장한다")
    void saveAll_success() {
        // given
        Organization organization1 = OrganizationFixture.createAllBlackBox();
        Organization organization2 = OrganizationFixture.createAllBlackBox();
        
        organizationRepository.save(organization1);
        organizationRepository.save(organization2);

        OrganizationCategory category1 = OrganizationCategoryFixture.createOrganizationCategory(organization1, SUGGESTION);
        OrganizationCategory category2 = OrganizationCategoryFixture.createOrganizationCategory(organization2, SUGGESTION);
        
        Set<OrganizationCategory> categories = Set.of(category1, category2);

        // when
        organizationCategoryService.saveAll(categories);

        // then
        List<OrganizationCategory> savedCategories = organizationCategoryRepository.findAll();
        assertThat(savedCategories).hasSize(2);
    }
}