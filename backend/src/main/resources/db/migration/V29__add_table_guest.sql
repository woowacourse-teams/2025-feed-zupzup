CREATE TABLE guest
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    visitor_uuid   BINARY(16) NOT NULL,
    connected_time DATETIME(6),
    created_at     DATETIME(6) NOT NULL,
    updated_at     DATETIME(6) NOT NULL
);