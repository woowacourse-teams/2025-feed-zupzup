CREATE TABLE voyage_retry_outbox
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    feedback_id      BIGINT       NOT NULL UNIQUE,
    error_message    TEXT,
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_created_at (created_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
    COMMENT = 'Voyage AI 재시도 Outbox (Redis 전송 후 즉시 삭제)';
