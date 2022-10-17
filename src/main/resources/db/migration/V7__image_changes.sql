ALTER TABLE image
    ADD name VARCHAR(255);

ALTER TABLE image
    ADD size FLOAT;

ALTER TABLE image
    ADD url VARCHAR(255);

ALTER TABLE image
    ADD CONSTRAINT uc_image_name UNIQUE (name);