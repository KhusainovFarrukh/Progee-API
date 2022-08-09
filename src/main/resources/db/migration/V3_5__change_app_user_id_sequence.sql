CREATE SEQUENCE IF NOT EXISTS app_user_id_sequence START WITH 1 INCREMENT BY 50;

ALTER TABLE app_user
    RENAME COLUMN id TO old_id;
ALTER TABLE app_user
    ADD COLUMN id BIGINT;
UPDATE app_user
SET id = old_id;

ALTER TABLE framework
    DROP CONSTRAINT fkt04ku5b48w1arfncfecypnlic;
ALTER TABLE language
    DROP CONSTRAINT fkhy7pj978ly64ydmodlbgdewkw;
ALTER TABLE review
    DROP CONSTRAINT fkse5kx11600wtv0jh9jobvrdpi;

ALTER TABLE app_user
    DROP CONSTRAINT users_pkey;
ALTER TABLE app_user
    DROP COLUMN old_id;
ALTER TABLE app_user
    ALTER COLUMN id SET NOT NULL;
ALTER TABLE app_user
    ADD CONSTRAINT app_user_pkey PRIMARY KEY (id);

ALTER TABLE framework
    ADD CONSTRAINT fkt04ku5b48w1arfncfecypnlic FOREIGN KEY (author_id) REFERENCES app_user (id);
ALTER TABLE language
    ADD CONSTRAINT fkhy7pj978ly64ydmodlbgdewkw FOREIGN KEY (author_id) REFERENCES app_user (id);
ALTER TABLE review
    ADD CONSTRAINT fkse5kx11600wtv0jh9jobvrdpi FOREIGN KEY (author_id) REFERENCES app_user (id);

DROP SEQUENCE IF EXISTS app_user_id_seq;
SELECT setval('app_user_id_sequence', max(id))
FROM app_user;