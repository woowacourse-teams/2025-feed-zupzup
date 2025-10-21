package feedzupzup.backend.organization.domain;

import feedzupzup.backend.category.domain.Category;
import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.global.BaseTimeEntity;
import feedzupzup.backend.organization.domain.vo.CheeringCount;
import feedzupzup.backend.organization.domain.vo.Name;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;
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

    @Embedded
    private OrganizationCategories organizationCategories;

    @Builder
    public Organization(
            final @NonNull UUID uuid,
            final @NonNull Name name,
            final @NonNull CheeringCount cheeringCount
    ) {
        this.uuid = uuid;
        this.name = name;
        this.cheeringCount = cheeringCount;
        this.organizationCategories = new OrganizationCategories();
    }

    public void cheer(final CheeringCount other) {
        this.cheeringCount.add(other);
    }

    public long getCheeringCountValue() {
        return cheeringCount.getValue();
    }

    public OrganizationCategory findOrganizationCategoryBy(final Category category) {
        return this.organizationCategories.findOrganizationCategoryBy(category);
    }

    public void addOrganizationCategories(final List<String> organizationCategories) {
        this.organizationCategories.addAll(organizationCategories, this);
    }

    public void updateOrganizationCategoriesAndName(
            final List<String> categories,
            final String name
    ) {
        this.organizationCategories.updateOrganizationCategories(categories);
        this.name = new Name(name);
    }
}
