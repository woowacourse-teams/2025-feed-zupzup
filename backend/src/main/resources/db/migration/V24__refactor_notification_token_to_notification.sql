ALTER TABLE notification_token RENAME TO notification;

ALTER TABLE notification CHANGE COLUMN registration_token token varchar(500) NOT NULL;
