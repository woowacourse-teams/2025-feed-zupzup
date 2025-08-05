package feedzupzup.backend.category.domain;

import feedzupzup.backend.global.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    public boolean isSameContent(String value) {
        return this.content.equals(value);
    }
}
