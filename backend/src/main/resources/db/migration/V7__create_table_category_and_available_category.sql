CREATE TABLE available_category (
    id bigint NOT NULL PRIMARY KEY,
    category_id bigint NOT NULL,
    organization_id bigint NOT NULL,
    CONSTRAINT FK_available_category_category_id FOREIGN KEY (category_id) REFERENCES category (id),
    CONSTRAINT FK_available_category_organization_id FOREIGN KEY (organization_id) REFERENCES organization (id)
);