ALTER TABLE feedback ADD COLUMN posted_at DATETIME;

-- 새로운 컬럼 추가로 인한 기존 데이터 null 값 처리 방안
UPDATE feedback
SET posted_at = created_at
WHERE posted_at IS NULL;