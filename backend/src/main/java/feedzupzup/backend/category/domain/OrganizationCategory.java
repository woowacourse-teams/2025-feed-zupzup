package feedzupzup.backend.category.domain;

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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrganizationCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Organization organization;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    public OrganizationCategory(
            final @NonNull Organization organization,
            final @NonNull Category category
    ) {
        this.organization = organization;
        this.category = category;
    }

    public boolean isSameCategory(final Category category) {
       return  this.category.equals(category);
    }
}
