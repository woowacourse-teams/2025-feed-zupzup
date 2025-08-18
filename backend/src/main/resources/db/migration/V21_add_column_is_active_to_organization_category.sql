ALTER TABLE organization_category ADD COLUMN is_active BIT(1) NOT NULL DEFAULT 1;

INSERT INTO organization_category (organization_id, category, is_active, created_at, modified_at, deleted_at)
values(1, 'FEEDBACK', 0,NOW(), NOW(),  null);

INSERT INTO organization_category (organization_id, category, is_active, created_at, modified_at, deleted_at)
values(1, 'COMPLIMENT', 0,NOW(), NOW(),null);

INSERT INTO organization_category (organization_id, category, is_active, created_at, modified_at, deleted_at)
values(1, 'SHARING', 0, NOW(), NOW(), null);