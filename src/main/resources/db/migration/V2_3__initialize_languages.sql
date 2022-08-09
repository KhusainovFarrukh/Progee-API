ALTER SEQUENCE languages_id_seq RESTART WITH 1;

INSERT INTO languages (state, image_id, author_id, created_at, name, description)
VALUES ('APPROVED', NULL, 1, NOW(), 'Java',
        'Java is a high-level, class-based, object-oriented programming language that is designed to have as few implementation dependencies as possible.');
INSERT INTO languages (state, image_id, author_id, created_at, name, description)
VALUES ('APPROVED', NULL, 1, NOW(), 'Kotlin',
        'A modern programming language that makes developers happier. · Multiplatform Mobile · Server-side · Web Frontend · Android.');
INSERT INTO languages (state, image_id, author_id, created_at, name, description)
VALUES ('APPROVED', NULL, 1, NOW(), 'Python',
        'Python is a high-level, interpreted, general-purpose programming language.');
INSERT INTO languages (state, image_id, author_id, created_at, name, description)
VALUES ('APPROVED', NULL, 1, NOW(), 'JavaScript',
        'JavaScript often abbreviated JS, is a programming language that is one of the core technologies of the World Wide Web, alongside HTML and CSS.');