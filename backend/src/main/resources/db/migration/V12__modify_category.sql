-- 1. feedback 테이블의 외래 키 제약 조건 제거
ALTER TABLE feedback DROP FOREIGN KEY FK_feedback_organization_category_id;

-- 2. organization_category 테이블의 id 컬럼에 AUTO_INCREMENT 속성 추가
ALTER TABLE organization_category MODIFY id BIGINT NOT NULL AUTO_INCREMENT;

-- 3. feedback 테이블에 외래 키 제약 조건 다시 추가
ALTER TABLE feedback ADD CONSTRAINT FK_feedback_organization_category_id
FOREIGN KEY (organization_category_id) REFERENCES organization_category (id);


ALTER TABLE organization_category MODIFY id BIGINT NOT NULL AUTO_INCREMENT;

-- 기존 카테고리 및 변경 카테고리 추가--
ALTER TABLE organization_category MODIFY category
    ENUM('FACILITY', 'ADMINISTRATION', 'CURRICULUM', 'REPORT', 'QUESTION', 'SUGGESTION', 'FEEDBACK', 'COMPLIMENT', 'SHARING', 'ETC');

-- organization_category에 새로운 카테고리 추가--
INSERT INTO organization_category (organization_id, category)
VALUES (1, 'REPORT');
INSERT INTO organization_category (organization_id, category)
VALUES (1, 'QUESTION');

-- ADMINISTRATION, CURRICULUM 을 ETC로 수정--
UPDATE feedback
SET organization_category_id = 3
WHERE organization_category_id IN (2, 4);

-- FACILITY를 SUGGESTION 으로 수정--
UPDATE organization_category
SET category = 'SUGGESTION'
WHERE category = 'FACILITY';

-- 기존 카테고리인 ADMINISTRATION, CURRICULUM 삭제--
DELETE
FROM organization_category
WHERE category IN ('ADMINISTRATION', 'CURRICULUM');

-- 새로 변경된 카테고리 리스트에서 기존 카테고리들 삭제--
ALTER TABLE organization_category MODIFY category
ENUM('REPORT', 'QUESTION', 'SUGGESTION', 'FEEDBACK', 'COMPLIMENT', 'SHARING', 'ETC');

