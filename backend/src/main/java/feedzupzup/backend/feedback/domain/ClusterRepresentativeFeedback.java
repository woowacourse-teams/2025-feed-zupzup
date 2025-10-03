package feedzupzup.backend.feedback.domain;

import java.util.UUID;

public record ClusterRepresentativeFeedback(
        String clusterId,
        String content,
        Long totalCount
) {

    public UUID id() {
        return UUID.fromString(clusterId);
    }
}
