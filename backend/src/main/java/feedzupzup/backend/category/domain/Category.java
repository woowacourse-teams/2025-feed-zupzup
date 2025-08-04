package feedzupzup.backend.category.domain;

import feedzupzup.backend.global.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Category extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "category")
    private final Set<AvailableCategory> availableCategories = new HashSet<>();

    private String content;

    public Category(
            final Long id,
            final String content
    ) {
        this.id = id;
        this.content = content;
    }

    public Category(final String content) {
        this(null, content);
    }

    public void addAvailableCategory(final AvailableCategory availableCategory) {
        this.availableCategories.add(availableCategory);
    }

    public boolean isSameContent(String value) {
        return this.content.equals(value);
    }
}
