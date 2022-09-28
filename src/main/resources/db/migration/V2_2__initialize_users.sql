ALTER SEQUENCE users_id_seq RESTART WITH 1;

INSERT INTO users (name, email, username, password, is_enabled, is_locked, role)
VALUES ('Farrukh Khusainov', 'farrukhbekkhusainov@gmail.com', 'farrukh_kh',
        '$2a$10$u.olISwSqjbaZCHADL0fIuw7eBijpqzvfSavgXnPcfniJTwORGNvm', TRUE, FALSE, 'SUPER_ADMIN'),
       ('Hamdam Xudayberganov', 'hamdam@mail.com', 'hamdam_x',
        '$2a$10$u.olISwSqjbaZCHADL0fIuw7eBijpqzvfSavgXnPcfniJTwORGNvm', TRUE, FALSE, 'ADMIN'),
       ('User Tester', 'user@mail.com', 'user_tester',
        '$2a$10$u.olISwSqjbaZCHADL0fIuw7eBijpqzvfSavgXnPcfniJTwORGNvm', TRUE, FALSE, 'USER');