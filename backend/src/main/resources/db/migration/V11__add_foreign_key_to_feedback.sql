ALTER TABLE feedback ADD CONSTRAINT fk_feedback_organization FOREIGN KEY (organization_id)
REFERENCES organization (id);