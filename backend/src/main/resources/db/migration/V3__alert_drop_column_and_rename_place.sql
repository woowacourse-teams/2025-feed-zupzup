ALTER TABLE place RENAME TO user_groups;

ALTER TABLE user_groups DROP COLUMN image_url;

ALTER TABLE feedback CHANGE COLUMN place_id group_id BIGINT;
