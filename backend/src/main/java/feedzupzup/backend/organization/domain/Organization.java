package feedzupzup.backend.organization.domain;

import feedzupzup.backend.global.BaseTimeEntity;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
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
public class Organization extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Embedded
    private CheeringCount cheeringCount;

    @Builder
    public Organization(final String name, final CheeringCount cheeringCount) {
        this.name = name;
        this.cheeringCount = cheeringCount;
    }

    public void cheer(final CheeringCount other) {
        this.cheeringCount.add(other);
    }
}
