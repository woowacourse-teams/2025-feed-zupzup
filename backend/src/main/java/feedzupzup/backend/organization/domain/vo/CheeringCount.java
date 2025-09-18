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

    private static final long ONCE_MAX_CHEERING_VALUE = 100;

    @Column(name = "cheering_count", nullable = false)
    private long value;

    public CheeringCount(final long value) {
        validateNonNegative(value);
        this.value = value;
    }

    public void validateNonNegative(final long value) {
        if (value < 0) {
            throw new OrganizationNumberException("응원 횟수는 음수의 값은 허용하지 않습니다.");
        }
    }

    public void add(final CheeringCount other) {
        validateNonNegative(other.value);
        if (other.value > ONCE_MAX_CHEERING_VALUE) {
            throw new OrganizationNumberException("응원 횟수는 한 번에 " + ONCE_MAX_CHEERING_VALUE +"를 초과할 수 없습니다.");
        }
        this.value += other.value;
    }
}
