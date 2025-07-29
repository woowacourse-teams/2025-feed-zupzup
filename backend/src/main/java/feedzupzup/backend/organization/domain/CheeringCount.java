package feedzupzup.backend.organization.domain;

import feedzupzup.backend.global.util.NumberValidator;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode(of = "value")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CheeringCount {

    @Column(name = "cheering_count", nullable = false)
    private int value;

    public CheeringCount(int value) {
        NumberValidator.validateNonNegative(value);
        this.value = value;
    }

    public void add(final CheeringCount other) {
        this.value += other.value;
    }
}
