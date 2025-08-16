ALTER TABLE feedback DROP FOREIGN KEY fk_feedback_organization;
ALTER TABLE organization_category DROP FOREIGN KEY FK_organization_category_organization_id;
ALTER TABLE organizer DROP FOREIGN KEY fk_organizer_organization;
ALTER TABLE qr DROP FOREIGN KEY fk_qr_organization;

ALTER TABLE organization MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT;

ALTER TABLE organization AUTO_INCREMENT = 2;

ALTER TABLE feedback ADD CONSTRAINT fk_feedback_organization 
    FOREIGN KEY (organization_id) REFERENCES organization(id);

ALTER TABLE organization_category ADD CONSTRAINT FK_organization_category_organization_id 
    FOREIGN KEY (organization_id) REFERENCES organization(id);

ALTER TABLE organizer ADD CONSTRAINT fk_organizer_organization 
    FOREIGN KEY (organization_id) REFERENCES organization(id) ON DELETE CASCADE;

ALTER TABLE qr ADD CONSTRAINT fk_qr_organization 
    FOREIGN KEY (organization_id) REFERENCES organization(id) ON DELETE CASCADE;
