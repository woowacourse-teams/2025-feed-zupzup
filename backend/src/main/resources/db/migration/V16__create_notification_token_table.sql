CREATE TABLE notification_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    registration_token VARCHAR(500) NOT NULL,
    admin_id BIGINT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT uk_notification_token_admin_id UNIQUE (admin_id),
    CONSTRAINT fk_notification_token_admin FOREIGN KEY (admin_id) REFERENCES admin(id) ON DELETE CASCADE
);