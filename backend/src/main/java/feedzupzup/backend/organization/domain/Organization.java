package feedzupzup.backend.organization.domain;

import feedzupzup.backend.category.domain.Category;
import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.global.BaseTimeEntity;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.organization.domain.vo.CheeringCount;
import feedzupzup.backend.organization.domain.vo.Name;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE organization SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Organization extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private UUID uuid;

    @Column(nullable = false)
    private Name name;

    @Embedded
    @Column(nullable = false)
    private CheeringCount cheeringCount;

    @Column(name = "deleted_at")
    protected LocalDateTime deletedAt;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<OrganizationCategory> organizationCategories = new HashSet<>();

    @Builder
    public Organization(
            final @NonNull UUID uuid,
            final @NonNull Name name,
            final @NonNull CheeringCount cheeringCount
    ) {
        this.uuid = uuid;
        this.name = name;
        this.cheeringCount = cheeringCount;
    }

    public void cheer(final CheeringCount other) {
        this.cheeringCount.add(other);
    }

    public int getCheeringCountValue() {
        return cheeringCount.getValue();
    }

    public OrganizationCategory findOrganizationCategoryBy(final Category category) {
        final OrganizationCategory resultCategory = organizationCategories.stream()
                .filter(organizationCategory -> organizationCategory.isSameCategory(category))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 카테고리입니다."));

        if (!resultCategory.isActive()) {
            throw new ResourceNotFoundException("해당 카테고리는 현재 비활성화 되어있습니다.");
        }
        return resultCategory;
    }

    public void addOrganizationCategories(final Set<OrganizationCategory> organizationCategories) {
        this.organizationCategories.addAll(organizationCategories);
    }

    public void updateOrganizationCategoriesAndName(
            final Set<OrganizationCategory> organizationCategories,
            final String name
    ) {
        this.organizationCategories.clear();
        this.organizationCategories.addAll(organizationCategories);
        this.name = new Name(name);
    }
}
