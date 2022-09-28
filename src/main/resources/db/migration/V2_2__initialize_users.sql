ALTER SEQUENCE users_id_seq RESTART WITH 1;

INSERT INTO users (name, email, username, password, is_enabled, is_locked, role)
VALUES ('Farrukh Khusainov', 'farrukhbekkhusainov@gmail.com', 'farrukh_kh',
        '${DEFAULT_ENCRYPTED_PASSWORD}', TRUE, FALSE, 'SUPER_ADMIN'),
       ('Hamdam Xudayberganov', 'hamdam@mail.com', 'hamdam_x',
        '${DEFAULT_ENCRYPTED_PASSWORD}', TRUE, FALSE, 'ADMIN'),
       ('User Tester', 'user@mail.com', 'user_tester',
        '${DEFAULT_ENCRYPTED_PASSWORD}', TRUE, FALSE, 'USER');