package feedzupzup.backend.organization.domain;

import feedzupzup.backend.category.domain.AvailableCategory;
import feedzupzup.backend.global.BaseTimeEntity;
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
    private String name;

    @Embedded
    @Column(nullable = false)
    private CheeringCount cheeringCount;

    @OneToMany(mappedBy = "organization")
    private final Set<AvailableCategory> availableCategories = new HashSet<>();

    @Builder
    public Organization(
            final @NonNull String name,
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

    public void addAvailableCategory(final AvailableCategory availableCategory) {
        this.availableCategories.add(availableCategory);
    }
}
