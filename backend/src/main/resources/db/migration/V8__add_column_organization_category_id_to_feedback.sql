ALTER TABLE feedback ADD COLUMN organization_category_id bigint NOT NULL DEFAULT 1;

ALTER TABLE feedback ADD CONSTRAINT FK_feedback_organization_category_id
    FOREIGN KEY (organization_category_id) REFERENCES organization_category (id);