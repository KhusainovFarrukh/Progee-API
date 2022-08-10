TRUNCATE TABLE role CASCADE;
ALTER SEQUENCE role_id_sequence RESTART WITH 1;

INSERT INTO role (id, title)
VALUES (nextval('role_id_sequence'), 'Super admin');

INSERT INTO role (id, title)
VALUES (currval('role_id_sequence') + 1, 'Admin');

INSERT INTO role (id, title)
VALUES (currval('role_id_sequence') + 2, 'User');

COMMIT;

INSERT INTO role_permissions (role_id, permission_name)
VALUES (1, 'CAN_VIEW_LANGUAGE');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (1, 'CAN_CREATE_LANGUAGE');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (1, 'CAN_UPDATE_LANGUAGE');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (1, 'CAN_DELETE_LANGUAGE');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (1, 'CAN_VIEW_FRAMEWORK');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (1, 'CAN_CREATE_FRAMEWORK');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (1, 'CAN_UPDATE_FRAMEWORK');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (1, 'CAN_DELETE_FRAMEWORK');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (1, 'CAN_VIEW_REVIEW');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (1, 'CAN_CREATE_REVIEW');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (1, 'CAN_UPDATE_REVIEW');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (1, 'CAN_DELETE_REVIEW');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (1, 'CAN_VIEW_USER');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (1, 'CAN_UPDATE_USER');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (1, 'CAN_DELETE_USER');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (1, 'CAN_VIEW_ROLE');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (1, 'CAN_CREATE_ROLE');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (1, 'CAN_UPDATE_ROLE');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (1, 'CAN_DELETE_ROLE');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (1, 'CAN_VIEW_IMAGE');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (1, 'CAN_CREATE_IMAGE');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (1, 'CAN_UPDATE_IMAGE');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (1, 'CAN_DELETE_IMAGE');

INSERT INTO role_permissions (role_id, permission_name)
VALUES (2, 'CAN_VIEW_LANGUAGE');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (2, 'CAN_CREATE_LANGUAGE');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (2, 'CAN_UPDATE_LANGUAGE');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (2, 'CAN_VIEW_FRAMEWORK');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (2, 'CAN_CREATE_FRAMEWORK');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (2, 'CAN_UPDATE_FRAMEWORK');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (2, 'CAN_VIEW_REVIEW');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (2, 'CAN_CREATE_REVIEW');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (2, 'CAN_UPDATE_REVIEW');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (2, 'CAN_DELETE_REVIEW');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (2, 'CAN_VIEW_USER');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (2, 'CAN_UPDATE_USER');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (2, 'CAN_VIEW_ROLE');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (2, 'CAN_VIEW_IMAGE');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (2, 'CAN_CREATE_IMAGE');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (2, 'CAN_UPDATE_IMAGE');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (2, 'CAN_DELETE_IMAGE');

INSERT INTO role_permissions (role_id, permission_name)
VALUES (3, 'CAN_VIEW_LANGUAGE');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (3, 'CAN_CREATE_LANGUAGE');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (3, 'CAN_UPDATE_LANGUAGE');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (3, 'CAN_VIEW_FRAMEWORK');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (3, 'CAN_CREATE_FRAMEWORK');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (3, 'CAN_UPDATE_FRAMEWORK');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (3, 'CAN_VIEW_REVIEW');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (3, 'CAN_CREATE_REVIEW');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (3, 'CAN_UPDATE_REVIEW');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (3, 'CAN_DELETE_REVIEW');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (3, 'CAN_VIEW_USER');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (3, 'CAN_UPDATE_USER');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (3, 'CAN_VIEW_ROLE');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (3, 'CAN_VIEW_IMAGE');
INSERT INTO role_permissions (role_id, permission_name)
VALUES (3, 'CAN_CREATE_IMAGE');
