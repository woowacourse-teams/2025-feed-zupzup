package feedzupzup.backend.organization.domain.vo;

import feedzupzup.backend.organization.domain.OrganizationException.OrganizationNumberException;
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

    @Column(name = "cheering_count")
    private int value;

    public CheeringCount(final int value) {
        validateNonNegative(value);
        this.value = value;
    }

    public void validateNonNegative(final int value) {
        if (value < 0) {
            throw new OrganizationNumberException("응원 횟수는 음수의 값은 허용하지 않습니다.");
        }
    }

    public void add(final CheeringCount other) {
        this.value += other.value;
    }
}
