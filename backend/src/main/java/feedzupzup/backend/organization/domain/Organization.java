package feedzupzup.backend.organization.domain;

import feedzupzup.backend.category.domain.Category;
import feedzupzup.backend.category.domain.OrganizationCategory;
import feedzupzup.backend.global.BaseTimeEntity;
import feedzupzup.backend.global.exception.ResourceException.ResourceNotFoundException;
import feedzupzup.backend.organization.domain.vo.CheeringCount;
import feedzupzup.backend.organization.domain.vo.Name;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Organization extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Name name;

    @Embedded
    @Column(nullable = false)
    private CheeringCount cheeringCount;

    @OneToMany(mappedBy = "organization")
    private final Set<OrganizationCategory> organizationCategories = new HashSet<>();

    @Builder
    public Organization(
            final @NonNull Name name,
            final @NonNull CheeringCount cheeringCount
    ) {
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
        return organizationCategories.stream()
                .filter(organizationCategory -> organizationCategory.isSameCategory(category))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 카테고리입니다."));
    }

    public void addOrganizationCategory(final OrganizationCategory organizationCategory) {
        this.organizationCategories.add(organizationCategory);
    }
}
