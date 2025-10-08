CREATE TABLE write_history
(
    id          BIGSERIAL PRIMARY KEY,
    guest_id    BIGINT NOT NULL,
    feedback_id BIGINT NOT NULL,
    created_at  DATETIME(6) NOT NULL,
    updated_at  DATETIME(6) NOT NULL,
    CONSTRAINT fk_write_history_guest FOREIGN KEY (guest_id) REFERENCES guest (id),
    CONSTRAINT fk_write_history_feedback FOREIGN KEY (feedback_id) REFERENCES feedback (id)
);