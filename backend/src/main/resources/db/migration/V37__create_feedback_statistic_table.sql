CREATE TABLE organization_statistic
(
    id                       BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    organization_id          BIGINT NOT NULL UNIQUE,
    feedback_total_count     BIGINT NOT NULL DEFAULT 0,
    feedback_confirmed_count BIGINT NOT NULL DEFAULT 0,
    feedback_waiting_count   BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_organization_statistic FOREIGN KEY (organization_id) REFERENCES organization(id)
)
