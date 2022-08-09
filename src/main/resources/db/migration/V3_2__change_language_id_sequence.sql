CREATE SEQUENCE IF NOT EXISTS language_id_sequence START WITH 1 INCREMENT BY 50;

ALTER TABLE language
    RENAME COLUMN id TO old_id;
ALTER TABLE language
    ADD COLUMN id BIGINT;
UPDATE language
SET id = old_id;

ALTER TABLE framework
    DROP CONSTRAINT fkqy32p17da1r14yq2ir757adnu;
ALTER TABLE review
    DROP CONSTRAINT fk4vb9m2r45v0hcmdmjd8fo681q;

ALTER TABLE language
    DROP CONSTRAINT languages_pkey;
ALTER TABLE language
    DROP COLUMN old_id;
ALTER TABLE language
    ALTER COLUMN id SET NOT NULL;
ALTER TABLE language
    ADD CONSTRAINT language_pkey PRIMARY KEY (id);

ALTER TABLE framework
    ADD CONSTRAINT fkqy32p17da1r14yq2ir757adnu FOREIGN KEY (language_id) REFERENCES language (id);
ALTER TABLE review
    ADD CONSTRAINT fk4vb9m2r45v0hcmdmjd8fo681q FOREIGN KEY (language_id) REFERENCES language (id);

DROP SEQUENCE IF EXISTS language_id_seq;
SELECT setval('language_id_sequence', max(id))
FROM language;