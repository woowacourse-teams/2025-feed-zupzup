-- 기존 feedback 테이블에서 클러스터링 관련 컬럼 제거
ALTER TABLE feedback 
    DROP COLUMN cluster_id,
    DROP COLUMN similarity_score,
    DROP COLUMN embedding_vector;

-- 임베딩 클러스터 테이블 생성
CREATE TABLE embedding_cluster
(
    id         BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    label      VARCHAR(255),
    deleted_at DATETIME(6)
);

-- 피드백 임베딩 클러스터 테이블 생성
CREATE TABLE feedback_embedding_cluster
(
    id               BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at       DATETIME(6) NOT NULL,
    modified_at      DATETIME(6) NOT NULL,
    similarity_score DOUBLE      NOT NULL,
    embedding_vector TEXT        NOT NULL,
    feedback_id      BIGINT      NOT NULL,
    embedding_cluster_id BIGINT  NOT NULL,
    deleted_at       DATETIME(6),
    CONSTRAINT fk_feedback_embedding_cluster_feedback 
        FOREIGN KEY (feedback_id) REFERENCES feedback (id),
    CONSTRAINT fk_feedback_embedding_cluster_embedding_cluster 
        FOREIGN KEY (embedding_cluster_id) REFERENCES embedding_cluster (id),
    CONSTRAINT uk_feedback_embedding_cluster_feedback 
        UNIQUE (feedback_id)
);
