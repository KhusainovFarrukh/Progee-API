UPDATE role_permissions
SET permission_name = 'CAN_UPDATE_OWN_LANGUAGE'
WHERE permission_name = 'CAN_UPDATE_LANGUAGE';

UPDATE role_permissions
SET permission_name = 'CAN_UPDATE_OWN_FRAMEWORK'
WHERE permission_name = 'CAN_UPDATE_FRAMEWORK';

UPDATE role_permissions
SET permission_name = 'CAN_UPDATE_OWN_REVIEW'
WHERE permission_name = 'CAN_UPDATE_REVIEW';

UPDATE role_permissions
SET permission_name = 'CAN_UPDATE_OWN_USER'
WHERE permission_name = 'CAN_UPDATE_USER';