package feedzupzup.backend.category.domain;

import feedzupzup.backend.organization.domain.Organization;
import jakarta.persistence.Entity;
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
public class AvailableCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    public AvailableCategory(
            final Long id,
            final @NonNull Organization organization,
            final @NonNull Category category
    ) {
        this.id = id;
        this.organization = organization;
        this.category = category;
    }

    public AvailableCategory(final Organization organization, final Category category) {
        this(null, organization, category);
    }

    public boolean isSameCategory(String value) {
        return category.isSameContent(value);
    }
}
