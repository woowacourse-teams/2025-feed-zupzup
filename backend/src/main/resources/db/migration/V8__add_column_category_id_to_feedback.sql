ALTER TABLE feedback ADD category_id bigint NOT NULL;
ALTER TABLE feedback ADD CONSTRAINT FK_feedback_category_id FOREIGN KEY (category_id) REFERENCES category (id);