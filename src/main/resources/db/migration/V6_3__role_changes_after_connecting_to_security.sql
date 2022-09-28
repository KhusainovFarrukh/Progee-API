ALTER TABLE role
    ADD is_default BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE app_user
    ADD role_id BIGINT;

ALTER TABLE app_user
    ADD CONSTRAINT fk_role_id_of_app_user FOREIGN KEY (role_id) REFERENCES role (id);

UPDATE app_user
SET role_id = 1
WHERE role = 'SUPER_ADMIN';

UPDATE app_user
SET role_id = 2
WHERE role = 'ADMIN';

UPDATE app_user
SET role_id = 3
WHERE role = 'USER';

ALTER TABLE app_user
    DROP COLUMN role;

UPDATE role
SET is_default = TRUE
WHERE id = 3;

INSERT INTO role_permissions (role_id, permission_name)
VALUES (1, 'CAN_SET_LANGUAGE_STATE'),
       (1, 'CAN_SET_FRAMEWORK_STATE'),
       (1, 'CAN_UPDATE_OTHERS_REVIEW'),
       (1, 'CAN_UPDATE_OTHER_USER'),
       (1, 'CAN_SET_USER_ROLE');

INSERT INTO role_permissions (role_id, permission_name)
VALUES (2, 'CAN_SET_LANGUAGE_STATE'),
       (2, 'CAN_SET_FRAMEWORK_STATE'),
       (2, 'CAN_UPDATE_OTHERS_REVIEW'),
       (2, 'CAN_UPDATE_OTHER_USER');