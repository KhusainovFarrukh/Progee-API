DELETE
FROM role_permissions
WHERE permission_name = 'CAN_VIEW_LANGUAGE'
   OR permission_name = 'CAN_VIEW_FRAMEWORK'
   OR permission_name = 'CAN_VIEW_REVIEW'
   OR permission_name = 'CAN_VIEW_USER'
   OR permission_name = 'CAN_VIEW_IMAGE'
   OR permission_name = 'CAN_CREATE_IMAGE';

INSERT INTO role_permissions (role_id, permission_name)
VALUES (1, 'CAN_UPDATE_OTHERS_LANGUAGE'),
       (1, 'CAN_UPDATE_OTHERS_FRAMEWORK');

INSERT INTO role_permissions (role_id, permission_name)
VALUES (2, 'CAN_UPDATE_OTHERS_LANGUAGE'),
       (2, 'CAN_UPDATE_OTHERS_FRAMEWORK');