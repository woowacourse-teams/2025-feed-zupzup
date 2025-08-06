package feedzupzup.backend.feedback.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Column(name = "comment")
    private String value;

    public Comment(final String value) {
        this.value = value;
    }

    public void updateComment(final String value) {
        this.value = value;
    }

}
