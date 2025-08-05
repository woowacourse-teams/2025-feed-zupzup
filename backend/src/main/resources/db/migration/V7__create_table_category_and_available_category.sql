CREATE TABLE organization_category (
    id bigint NOT NULL PRIMARY KEY,
    organization_id bigint NOT NULL,
    category ENUM('CURRICULUM','ETC','FACILITY') NOT NULL,
    CONSTRAINT FK_organization_category_organization_id FOREIGN KEY (organization_id) REFERENCES organization (id)
);