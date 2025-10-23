CREATE TABLE like_history
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    guest_id    BIGINT NOT NULL,
    feedback_id BIGINT NOT NULL,
    created_at  DATETIME(6) NOT NULL,
    modified_at  DATETIME(6) NOT NULL,
    CONSTRAINT fk_like_history_guest FOREIGN KEY (guest_id) REFERENCES guest (id),
    CONSTRAINT fk_like_history_feedback FOREIGN KEY (feedback_id) REFERENCES feedback (id)
);