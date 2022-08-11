UPDATE role_permissions
SET permission_name = 'CAN_DELETE_REVIEW'
WHERE permission_name = 'CAN_DELETE_OWN_REVIEW';