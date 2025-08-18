package feedzupzup.backend.category.domain;

import feedzupzup.backend.global.BaseTimeEntity;
import feedzupzup.backend.organization.domain.Organization;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SQLDelete(sql = "UPDATE organization_category SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class OrganizationCategory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Organization organization;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(nullable = false)
    private boolean isActive;

    @Column(name = "deleted_at")
    protected LocalDateTime deletedAt;

    public OrganizationCategory(
            final @NonNull Organization organization,
            final @NonNull Category category,
            final boolean isActive
    ) {
        this.organization = organization;
        this.category = category;
        this.isActive = isActive;
    }

    public boolean isSameCategory(final Category category) {
       return this.category.equals(category);
    }

    public void modifyUpdateStatus(final boolean activeStatus) {
        this.isActive = activeStatus;
    }
}
