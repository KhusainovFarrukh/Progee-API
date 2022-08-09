ALTER TABLE review_up_votes
    RENAME COLUMN up_votes TO up_voter_id;
ALTER TABLE review_down_votes
    RENAME COLUMN down_votes TO down_voter_id;

ALTER TABLE review_up_votes
    RENAME CONSTRAINT fk2hog4j8bngr0xh1wvs9rg027j TO fk_review_id_of_up_votes;

ALTER TABLE review
    RENAME CONSTRAINT fk4vb9m2r45v0hcmdmjd8fo681q TO fk_language_id_of_review;

ALTER TABLE review_down_votes
    RENAME CONSTRAINT fkgt4md3q75fjt1k3pwt5a187v4 TO fk_review_id_of_down_votes;

ALTER TABLE app_user
    RENAME CONSTRAINT fklqj25c28swu46s4jbudd7hore TO fk_image_id_of_app_user;

ALTER TABLE framework
    RENAME CONSTRAINT fkqy32p17da1r14yq2ir757adnu TO fk_language_id_of_framework;