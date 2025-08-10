package feedzupzup.backend.organization.domain.vo;

import feedzupzup.backend.organization.domain.OrganizationException.OrganizationLengthException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Name {

    private static final int MAX_LENGTH = 10;

    @Column(name = "name")
    private String value;

    public Name(final String value) {
        validateLength(value);
        this.value = value;
    }

    public void validateLength(String value) {
        if (value.length() > MAX_LENGTH) {
            throw new OrganizationLengthException(
                    "organization name length은 " + MAX_LENGTH + "를 초과할 수 없습니다.");
        }
    }
}
