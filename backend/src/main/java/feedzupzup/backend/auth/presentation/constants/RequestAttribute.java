package feedzupzup.backend.auth.presentation.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RequestAttribute {

    GUEST_ID("guest_id"),
    ;

    private final String value;
}
