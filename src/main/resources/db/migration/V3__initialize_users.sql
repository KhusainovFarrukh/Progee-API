ALTER SEQUENCE users_id_seq RESTART WITH 1;

INSERT INTO users (name, email, username, password, is_enabled, is_locked, role)
VALUES ('Farrukh Khusainov', 'farrukhbekkhusainov@gmail.com', 'farrukh_kh', '12345678', TRUE, FALSE, 'SUPER_ADMIN');
INSERT INTO users (name, email, username, password, is_enabled, is_locked, role)
VALUES ('Hamdam Xudayberganov', 'hamdam@mail.com', 'hamdam_x', '12345678', TRUE, FALSE, 'ADMIN');
INSERT INTO users (name, email, username, password, is_enabled, is_locked, role)
VALUES ('User Tester', 'user@mail.com', 'user_tester', '12345678', TRUE, FALSE, 'USER');