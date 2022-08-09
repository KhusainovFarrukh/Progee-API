CREATE SEQUENCE IF NOT EXISTS framework_id_sequence START WITH 1 INCREMENT BY 50;

ALTER TABLE framework
    RENAME COLUMN id TO old_id;
ALTER TABLE framework
    ADD COLUMN id BIGINT;
UPDATE framework
SET id = old_id;

ALTER TABLE framework
    DROP CONSTRAINT frameworks_pkey;
ALTER TABLE framework
    DROP COLUMN old_id;
ALTER TABLE framework
    ALTER COLUMN id SET NOT NULL;
ALTER TABLE framework
    ADD CONSTRAINT framework_pkey PRIMARY KEY (id);

DROP SEQUENCE IF EXISTS framework_id_seq;
SELECT setval('framework_id_sequence', max(id))
FROM framework;