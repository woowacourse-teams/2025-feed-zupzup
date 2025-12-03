package feedzupzup.backend.category.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

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

    @DisplayName("OrganizationCategory 생성 시 organization이 null이면 예외가 발생한다")
    @Test
    void constructor_organizationNull_throwsException() {
        // when & then
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new OrganizationCategory(null, Category.ETC, true));
    }

    @DisplayName("OrganizationCategory 생성 시 category가 null이면 예외가 발생한다")
    @Test
    void constructor_categoryNull_throwsException() {
        // given
        Organization organization = OrganizationFixture.createAllBlackBox();
        Organization savedOrganization = organizationRepository.save(organization);

        // when & then
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> new OrganizationCategory(savedOrganization, null, true));
    }

    @DisplayName("OrganizationCategory 생성 시 isActive가 true인 경우 정상 생성된다")
    @Test
    void constructor_isActiveTrue_success() {
        // given
        Organization organization = OrganizationFixture.createAllBlackBox();
        Organization savedOrganization = organizationRepository.save(organization);

        // when
        OrganizationCategory organizationCategory = new OrganizationCategory(
                savedOrganization,
                Category.ETC,
                true
        );
        OrganizationCategory saved = organizationCategoryRepository.save(organizationCategory);

        // then
        assertThat(saved.isActive()).isTrue();
        assertThat(saved.getCategory()).isEqualTo(Category.ETC);
        assertThat(saved.getOrganization()).isEqualTo(savedOrganization);
    }

    @DisplayName("OrganizationCategory 생성 시 isActive가 false인 경우 정상 생성된다")
    @Test
    void constructor_isActiveFalse_success() {
        // given
        Organization organization = OrganizationFixture.createAllBlackBox();
        Organization savedOrganization = organizationRepository.save(organization);

        // when
        OrganizationCategory organizationCategory = new OrganizationCategory(
                savedOrganization,
                Category.ETC,
                false
        );
        OrganizationCategory saved = organizationCategoryRepository.save(organizationCategory);

        // then
        assertThat(saved.isActive()).isFalse();
        assertThat(saved.getCategory()).isEqualTo(Category.ETC);
        assertThat(saved.getOrganization()).isEqualTo(savedOrganization);
    }
}
