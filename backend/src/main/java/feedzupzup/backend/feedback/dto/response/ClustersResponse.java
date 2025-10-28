package feedzupzup.backend.feedback.dto.response;

import feedzupzup.backend.feedback.domain.ClusterInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record ClustersResponse(
        @Schema(description = " 클러스터 대표 전체 목록")
        List<ClusterResponse> clusterInfos
) {

    public record ClusterResponse(
            @Schema(description = "클러스터 ID", example = "1")
            Long clusterId,
            @Schema(description = "클러스터 대표 글", example = "클러스터의 대표 글입니다")
            String label,
            @Schema(description = "클러스터에 속한 피드백 총 개수", example = "5")
            Long totalCount
    ) {

        public static ClusterResponse from(ClusterInfo cluster) {
            return new ClusterResponse(cluster.embeddingClusterId(), cluster.label(), cluster.totalCount());
        }
    }

    public static ClustersResponse from(List<ClusterInfo> clusterInfos) {
        List<ClusterResponse> responses = clusterInfos.stream()
                .map(ClusterResponse::from)
                .toList();
        return new ClustersResponse(responses);
    }
}
