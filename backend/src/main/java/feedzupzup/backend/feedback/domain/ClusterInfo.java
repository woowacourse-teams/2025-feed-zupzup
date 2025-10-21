package feedzupzup.backend.feedback.domain;

public record ClusterInfo(
        Long embeddingClusterId,
        String label,
        Long totalCount
) {
}
