ALTER TABLE feedback ADD COLUMN organization_category_id bigint;
UPDATE feedback SET organization_category_id = 1 WHERE organization_category_id IS NULL;
ALTER TABLE feedback MODIFY organization_category_id bigint NOT NULL;

ALTER TABLE feedback ADD CONSTRAINT FK_feedback_organization_category_id
    FOREIGN KEY (organization_category_id) REFERENCES organization_category (id);