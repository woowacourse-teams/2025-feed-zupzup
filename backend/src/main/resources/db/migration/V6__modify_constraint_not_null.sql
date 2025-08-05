ALTER TABLE feedback MODIFY status ENUM('CONFIRMED','WAITING') NOT NULL;

ALTER TABLE feedback
    MODIFY like_count int NOT NULL,
    MODIFY content varchar(255) NOT NULL,
    MODIFY organization_id bigint NOT NULL;

ALTER TABLE feedback MODIFY posted_at datetime(6) NOT NULL;
