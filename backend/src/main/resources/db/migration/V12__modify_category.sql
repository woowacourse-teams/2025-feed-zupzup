ALTER TABLE organization_category MODIFY category
    ENUM('FACILITY', 'ADMINISTRATION', 'CURRICULUM', 'REPORT', 'QUESTION', 'SUGGESTION', 'FEEDBACK', 'COMPLIMENT', 'SHARING', 'ETC');

INSERT INTO organization_category (organization_id, category)
VALUES (1, 'REPORT');
INSERT INTO organization_category (organization_id, category)
VALUES (1, 'QUESTION');
INSERT INTO organization_category (organization_id, category)
VALUES (1, 'SUGGESTION');

UPDATE feedback
SET organization_category_id = 3
WHERE organization_category_id IN (2, 4);

UPDATE organization_category
SET category = 'SUGGESTION'
WHERE category = 'FACILITY';

DELETE
FROM organization_category
WHERE category IN ('ADMINISTRATION', 'CURRICULUM');

ALTER TABLE organization_category MODIFY category
ENUM('REPORT', 'QUESTION', 'SUGGESTION', 'FEEDBACK', 'COMPLIMENT', 'SHARING', 'ETC');

