-- Admin 테이블의 login_id 컬럼 길이를 10에서 100으로 변경
ALTER TABLE admin MODIFY COLUMN login_id VARCHAR(100) NOT NULL UNIQUE;
