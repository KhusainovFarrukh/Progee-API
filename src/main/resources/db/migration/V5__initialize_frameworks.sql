ALTER SEQUENCE frameworks_id_seq RESTART WITH 1;

INSERT INTO frameworks (state, image_id, author_id, created_at, name, description, language_id)
VALUES ('APPROVED', NULL, 1, NOW(), 'Spring Boot',
        'Spring Boot — The Spring Framework is an application framework and inversion of control container for the Java platform.',
        1);
INSERT INTO frameworks (state, image_id, author_id, created_at, name, description, language_id)
VALUES ('APPROVED', NULL, 1, NOW(), 'JRockit',
        'Java profiling tool for performance Tuning.',
        1);
INSERT INTO frameworks (state, image_id, author_id, created_at, name, description, language_id)
VALUES ('APPROVED', NULL, 1, NOW(), 'JSoup',
        'Java HTML parser library. Supports extracting and manipulating data using DOM, CSS, and JQuery methods.',
        1);
INSERT INTO frameworks (state, image_id, author_id, created_at, name, description, language_id)
VALUES ('APPROVED', NULL, 1, NOW(), 'Ktor',
        'Ktor is a framework for quickly creating web applications in Kotlin with minimal effort.',
        2);
INSERT INTO frameworks (state, image_id, author_id, created_at, name, description, language_id)
VALUES ('APPROVED', NULL, 1, NOW(), 'Kweb',
        'Kweb is a new way to create beautiful, efficient, and scalable websites in Kotlin, quickly.',
        2);
VALUES ('APPROVED', NULL, 1, NOW(), 'Django',
        'Django is the most popular high-level web application development framework that encourages us to build Python applications very quickly.',
        3);
INSERT INTO frameworks (state, image_id, author_id, created_at, name, description, language_id)
VALUES ('APPROVED', NULL, 1, NOW(), 'CubicWeb',
        'CubicWeb is an open-source, semantic, and free Python web framework.',
        3);
