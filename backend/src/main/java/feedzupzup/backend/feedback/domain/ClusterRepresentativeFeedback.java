package feedzupzup.backend.feedback.domain;

import java.util.UUID;

public record ClusterRepresentativeFeedback(
        UUID clusterId,
        String content,
        Long totalCount
) {
}
