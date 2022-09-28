TRUNCATE TABLE role CASCADE;
ALTER SEQUENCE role_id_sequence RESTART WITH 1;

SELECT nextval('role_id_sequence');

INSERT INTO role (id, title)
VALUES (currval('role_id_sequence'), 'Super admin'),
       (currval('role_id_sequence') + 1, 'Admin'),
       (currval('role_id_sequence') + 2, 'User');

SELECT setval('role_id_sequence', currval('role_id_sequence') + 2);

COMMIT;

INSERT INTO role_permissions (role_id, permission_name)
VALUES (1, 'CAN_VIEW_LANGUAGE'),
       (1, 'CAN_CREATE_LANGUAGE'),
       (1, 'CAN_UPDATE_LANGUAGE'),
       (1, 'CAN_DELETE_LANGUAGE'),
       (1, 'CAN_VIEW_FRAMEWORK'),
       (1, 'CAN_CREATE_FRAMEWORK'),
       (1, 'CAN_UPDATE_FRAMEWORK'),
       (1, 'CAN_DELETE_FRAMEWORK'),
       (1, 'CAN_VIEW_REVIEW'),
       (1, 'CAN_CREATE_REVIEW'),
       (1, 'CAN_UPDATE_REVIEW'),
       (1, 'CAN_DELETE_REVIEW'),
       (1, 'CAN_VIEW_USER'),
       (1, 'CAN_UPDATE_USER'),
       (1, 'CAN_DELETE_USER'),
       (1, 'CAN_VIEW_ROLE'),
       (1, 'CAN_CREATE_ROLE'),
       (1, 'CAN_UPDATE_ROLE'),
       (1, 'CAN_DELETE_ROLE'),
       (1, 'CAN_VIEW_IMAGE'),
       (1, 'CAN_CREATE_IMAGE'),
       (1, 'CAN_UPDATE_IMAGE'),
       (1, 'CAN_DELETE_IMAGE');

INSERT INTO role_permissions (role_id, permission_name)
VALUES (2, 'CAN_VIEW_LANGUAGE'),
       (2, 'CAN_CREATE_LANGUAGE'),
       (2, 'CAN_UPDATE_LANGUAGE'),
       (2, 'CAN_VIEW_FRAMEWORK'),
       (2, 'CAN_CREATE_FRAMEWORK'),
       (2, 'CAN_UPDATE_FRAMEWORK'),
       (2, 'CAN_VIEW_REVIEW'),
       (2, 'CAN_CREATE_REVIEW'),
       (2, 'CAN_UPDATE_REVIEW'),
       (2, 'CAN_DELETE_REVIEW'),
       (2, 'CAN_VIEW_USER'),
       (2, 'CAN_UPDATE_USER'),
       (2, 'CAN_VIEW_ROLE'),
       (2, 'CAN_VIEW_IMAGE'),
       (2, 'CAN_CREATE_IMAGE'),
       (2, 'CAN_UPDATE_IMAGE'),
       (2, 'CAN_DELETE_IMAGE');

INSERT INTO role_permissions (role_id, permission_name)
VALUES (3, 'CAN_VIEW_LANGUAGE'),
       (3, 'CAN_CREATE_LANGUAGE'),
       (3, 'CAN_UPDATE_LANGUAGE'),
       (3, 'CAN_VIEW_FRAMEWORK'),
       (3, 'CAN_CREATE_FRAMEWORK'),
       (3, 'CAN_UPDATE_FRAMEWORK'),
       (3, 'CAN_VIEW_REVIEW'),
       (3, 'CAN_CREATE_REVIEW'),
       (3, 'CAN_UPDATE_REVIEW'),
       (3, 'CAN_DELETE_REVIEW'),
       (3, 'CAN_VIEW_USER'),
       (3, 'CAN_UPDATE_USER'),
       (3, 'CAN_VIEW_ROLE'),
       (3, 'CAN_VIEW_IMAGE'),
       (3, 'CAN_CREATE_IMAGE');
