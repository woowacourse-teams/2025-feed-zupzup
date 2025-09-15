-- Rename notification_token table to notification and rename value column to token
RENAME TABLE notification_token TO notification;

ALTER TABLE notification
CHANGE COLUMN value token VARCHAR(500) NOT NULL;