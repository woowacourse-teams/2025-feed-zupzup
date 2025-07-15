package feedzupzup.backend.place;

import feedzupzup.backend.global.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place extends BaseTimeEntity {
    @Id
    @Column(nullable = false)
    private Long id;

    private String name;

    private String imageUrl;

}
