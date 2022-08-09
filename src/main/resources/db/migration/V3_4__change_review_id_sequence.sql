CREATE SEQUENCE IF NOT EXISTS review_id_sequence START WITH 1 INCREMENT BY 50;

ALTER TABLE review
    RENAME COLUMN id TO old_id;
ALTER TABLE review
    ADD COLUMN id BIGINT;
UPDATE review
SET id = old_id;

ALTER TABLE review_down_votes
    DROP CONSTRAINT fkgt4md3q75fjt1k3pwt5a187v4;
ALTER TABLE review_up_votes
    DROP CONSTRAINT fk2hog4j8bngr0xh1wvs9rg027j;

ALTER TABLE review
    DROP CONSTRAINT reviews_pkey;
ALTER TABLE review
    DROP COLUMN old_id;
ALTER TABLE review
    ALTER COLUMN id SET NOT NULL;
ALTER TABLE review
    ADD CONSTRAINT review_pkey PRIMARY KEY (id);

ALTER TABLE review_down_votes
    ADD CONSTRAINT fkgt4md3q75fjt1k3pwt5a187v4 FOREIGN KEY (review_id) REFERENCES review (id);
ALTER TABLE review_up_votes
    ADD CONSTRAINT fk2hog4j8bngr0xh1wvs9rg027j FOREIGN KEY (review_id) REFERENCES review (id);

DROP SEQUENCE IF EXISTS review_id_seq;
SELECT setval('review_id_sequence', max(id))
FROM review;