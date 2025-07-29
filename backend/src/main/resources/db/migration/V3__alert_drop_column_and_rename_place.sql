ALTER TABLE place RENAME TO organization;

ALTER TABLE organization DROP COLUMN image_url;

ALTER TABLE feedback CHANGE COLUMN place_id organization_id BIGINT;
