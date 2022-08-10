CREATE SEQUENCE IF NOT EXISTS role_id_sequence START WITH 1 INCREMENT BY 50;

CREATE TABLE role
(
    id    BIGINT       NOT NULL,
    title VARCHAR(255) NOT NULL,
    CONSTRAINT pk_role PRIMARY KEY (id)
);

CREATE TABLE role_permissions
(
    role_id         BIGINT NOT NULL,
    permission_name VARCHAR(255)
);

ALTER TABLE role
    ADD CONSTRAINT uk_role_title UNIQUE (title);

ALTER TABLE role_permissions
    ADD CONSTRAINT fk_role_id_of_permission FOREIGN KEY (role_id) REFERENCES role (id);