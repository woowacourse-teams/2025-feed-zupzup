ALTER TABLE feedback
    ADD COLUMN cluster_id BINARY(16) NULL,
    ADD COLUMN similarity_score DOUBLE NULL,
    ADD COLUMN embedding_vector TEXT NULL

