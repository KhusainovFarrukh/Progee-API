ALTER TABLE review
    RENAME CONSTRAINT fkse5kx11600wtv0jh9jobvrdpi TO fk_author_id_of_review;

ALTER TABLE language
    RENAME CONSTRAINT fkn1gk8lkuf5dv0it3p1law4i9t TO fk_image_id_of_language;

ALTER TABLE language
    RENAME CONSTRAINT fkhy7pj978ly64ydmodlbgdewkw TO fk_author_id_of_language;

ALTER TABLE framework
    RENAME CONSTRAINT fkm4ga66uiqf2rt6ynucwwb1c83 TO fk_image_id_of_framework;

ALTER TABLE framework
    RENAME CONSTRAINT fkt04ku5b48w1arfncfecypnlic TO fk_author_id_of_framework;