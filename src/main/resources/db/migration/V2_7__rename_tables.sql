ALTER TABLE IF EXISTS users
    RENAME TO app_user;
ALTER TABLE IF EXISTS languages
    RENAME TO language;
ALTER TABLE IF EXISTS frameworks
    RENAME TO framework;
ALTER TABLE IF EXISTS reviews
    RENAME TO review;
ALTER TABLE IF EXISTS images
    RENAME TO image;

ALTER SEQUENCE IF EXISTS users_id_seq
    RENAME TO app_user_id_seq;
ALTER SEQUENCE IF EXISTS languages_id_seq
    RENAME TO language_id_seq;
ALTER SEQUENCE IF EXISTS frameworks_id_seq
    RENAME TO framework_id_seq;
ALTER SEQUENCE IF EXISTS reviews_id_seq
    RENAME TO review_id_seq;
ALTER SEQUENCE IF EXISTS images_id_seq
    RENAME TO image_id_seq;