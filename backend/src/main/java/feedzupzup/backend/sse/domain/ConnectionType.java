package feedzupzup.backend.sse.domain;

import lombok.Getter;

@Getter
public enum ConnectionType {
    GUEST("GUEST"),
    ADMIN("ADMIN");

    private final String prefix;

    ConnectionType(final String prefix) {
        this.prefix = prefix;
    }
}
