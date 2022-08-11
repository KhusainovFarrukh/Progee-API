UPDATE role_permissions
SET permission_name = 'CAN_VOTE_REVIEW'
WHERE permission_name = 'CAN_UPVOTE_REVIEW';

DELETE
FROM role_permissions
WHERE permission_name = 'CAN_DOWNVOTE_REVIEW';