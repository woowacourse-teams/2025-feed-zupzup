package feedzupzup.backend.feedback.domain;

import feedzupzup.backend.global.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feedback extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private boolean isSecret;

    @Enumerated(EnumType.STRING)
    private ProcessStatus status;

    private Long placeId;

    private String imageUrl;

    @Builder
    public Feedback(String content, boolean isSecret, ProcessStatus status, Long placeId, String imageUrl) {
        this.content = content;
        this.isSecret = isSecret;
        this.status = status;
        this.placeId = placeId;
        this.imageUrl = imageUrl;
    }

    public void updateStatus(final ProcessStatus status) {
        this.status = status;
    }
}
