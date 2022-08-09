CREATE SEQUENCE IF NOT EXISTS image_id_sequence START WITH 1 INCREMENT BY 50;

ALTER TABLE image
    RENAME COLUMN id TO old_id;
ALTER TABLE image
    ADD COLUMN id BIGINT;
UPDATE image
SET id = old_id;

ALTER TABLE app_user
    DROP CONSTRAINT fklqj25c28swu46s4jbudd7hore;
ALTER TABLE framework
    DROP CONSTRAINT fkm4ga66uiqf2rt6ynucwwb1c83;
ALTER TABLE language
    DROP CONSTRAINT fkn1gk8lkuf5dv0it3p1law4i9t;

ALTER TABLE image
    DROP CONSTRAINT image_pkey;
ALTER TABLE image
    DROP COLUMN old_id;
ALTER TABLE image
    ALTER COLUMN id SET NOT NULL;
ALTER TABLE image
    ADD CONSTRAINT image_pkey PRIMARY KEY (id);

ALTER TABLE app_user
    ADD CONSTRAINT fklqj25c28swu46s4jbudd7hore FOREIGN KEY (image_id) REFERENCES image (id);
ALTER TABLE framework
    ADD CONSTRAINT fkm4ga66uiqf2rt6ynucwwb1c83 FOREIGN KEY (image_id) REFERENCES image (id);
ALTER TABLE language
    ADD CONSTRAINT fkn1gk8lkuf5dv0it3p1law4i9t FOREIGN KEY (image_id) REFERENCES image (id);

DROP SEQUENCE IF EXISTS image_id_seq;
SELECT setval('image_id_sequence', max(id))
FROM image;