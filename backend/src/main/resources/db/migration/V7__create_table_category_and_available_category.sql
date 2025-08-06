CREATE TABLE organization_category (
    id bigint NOT NULL PRIMARY KEY,
    organization_id bigint NOT NULL,
    category ENUM('CURRICULUM', 'ADMINISTRATION', 'ETC', 'FACILITY') NOT NULL,
    CONSTRAINT FK_organization_category_organization_id FOREIGN KEY (organization_id) REFERENCES organization (id)
);

INSERT INTO organization_category values (1, 1, 'CURRICULUM');
INSERT INTO organization_category values (2, 1, 'ADMINISTRATION');
INSERT INTO organization_category values (3, 1, 'ETC');
INSERT INTO organization_category values (4, 1, 'FACILITY');