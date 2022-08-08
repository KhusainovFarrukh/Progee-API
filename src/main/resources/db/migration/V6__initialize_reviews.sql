ALTER SEQUENCE reviews_id_seq RESTART WITH 1;

INSERT INTO reviews (author_id, created_at, body, review_value, language_id)
VALUES (1, NOW(),
        'Java is a great language. It has many features, it''s safe, platform independent, and of course, it''s slow',
        2, 1);
INSERT INTO reviews (author_id, created_at, body, review_value, language_id)
VALUES (2, NOW(), 'I have been working in Java technologies for more than 10 years', 2, 1);
INSERT INTO reviews (author_id, created_at, body, review_value, language_id)
VALUES (3, NOW(), 'it is the “Beige Volvo Estate Wagon” of programming languages…slow, boring, reliable, safe…yawn', -1,
        1);
INSERT INTO reviews (author_id, created_at, body, review_value, language_id)
VALUES (2, NOW(), 'People say it is like Java, but much better', 1, 2);
INSERT INTO reviews (author_id, created_at, body, review_value, language_id)
VALUES (2, NOW(),
        'Python is immensely useful for companies using multiple software and vendors. The overall experience is very satisfactory as the product is extremely easy to learn',
        2, 3);