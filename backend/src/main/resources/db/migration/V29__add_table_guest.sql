CREATE TABLE guest
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    guest_uuid   BINARY(16) NOT NULL UNIQUE,
    connected_time DATETIME(6),
    created_at     DATETIME(6) NOT NULL,
    updated_at     DATETIME(6) NOT NULL
);