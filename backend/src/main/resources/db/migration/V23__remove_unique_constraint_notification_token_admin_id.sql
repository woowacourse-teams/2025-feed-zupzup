ALTER TABLE notification_token DROP FOREIGN KEY fk_notification_token_admin;

ALTER TABLE notification_token DROP INDEX uk_notification_token_admin_id;

ALTER TABLE notification_token ADD CONSTRAINT fk_notification_token_admin
    FOREIGN KEY (admin_id) REFERENCES admin(id) ON DELETE CASCADE;
