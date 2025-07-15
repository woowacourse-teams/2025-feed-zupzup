package feedzupzup.backend.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    TEST("TEST-001", "testErrorMessage"),
    ;

    private final String code;
    private final String message;
}
