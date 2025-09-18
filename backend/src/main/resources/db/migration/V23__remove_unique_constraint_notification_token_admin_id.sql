ALTER TABLE notification_token DROP FOREIGN KEY IF EXISTS fk_notification_token_admin;

DROP INDEX IF EXISTS uk_notification_token_admin_id ON notification_token;

ALTER TABLE notification_token ADD CONSTRAINT fk_notification_token_admin
    FOREIGN KEY (admin_id) REFERENCES admin(id) ON DELETE CASCADE;
