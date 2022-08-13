UPDATE role_permissions
SET permission_name = 'CAN_DELETE_OWN_REVIEW'
WHERE permission_name = 'CAN_DELETE_REVIEW';

INSERT INTO role_permissions (role_id, permission_name)
VALUES (1, 'CAN_DELETE_OTHERS_REVIEW');

INSERT INTO role_permissions (role_id, permission_name)
VALUES (2, 'CAN_DELETE_OTHERS_REVIEW');