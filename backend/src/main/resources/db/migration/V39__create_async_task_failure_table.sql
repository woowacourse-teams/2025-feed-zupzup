CREATE TABLE async_task_failure
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_type     VARCHAR(50)  NOT NULL,
    target_type   VARCHAR(20)  NOT NULL,
    target_id     VARCHAR(100) NOT NULL,
    error_message TEXT         NOT NULL,
    is_retryable  BIT(1)      NOT NULL,
    retry_count   INT          NOT NULL DEFAULT 0,
    status        VARCHAR(20)  NOT NULL,
    created_at    DATETIME(6)  NOT NULL,
    modified_at    DATETIME(6)  NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
