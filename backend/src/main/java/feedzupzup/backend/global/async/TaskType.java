package feedzupzup.backend.global.async;

public enum TaskType {
    FEEDBACK_CLUSTERING("피드백 클러스터링"),
    CLUSTER_LABEL_GENERATION("클러스터 라벨 생성");

    private final String description;

    TaskType(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}