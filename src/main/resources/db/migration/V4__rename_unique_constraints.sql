ALTER TABLE framework
    RENAME CONSTRAINT uk_hjbth9dc342cn7dqo0l2s1ftq TO uk_framework_name;

ALTER TABLE language
    RENAME CONSTRAINT uk_f6axmaokhmrbmm746866v0uyu TO uk_language_name;

ALTER TABLE app_user
    RENAME CONSTRAINT uk_6dotkott2kjsp8vw4d0m25fb7 TO uk_app_user_email;

ALTER TABLE app_user
    RENAME CONSTRAINT uk_r43af9ap4edm43mmtq01oddj6 TO uk_app_user_username;

ALTER TABLE app_user
    ALTER COLUMN is_enabled SET NOT NULL;

ALTER TABLE app_user
    ALTER COLUMN is_locked SET NOT NULL;