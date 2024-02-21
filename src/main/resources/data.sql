INSERT INTO roles (name)
VALUES ('ROLE_ADMIN'), ('ROLE_USER');

--username: test_user
--password: sausages
INSERT INTO users (username, password, email)
VALUES
    ('test_user' ,'$2a$10$PcSvcvAMh0UjiS8CsiNbzulmxR4ua0g3PDg.eNQGTfwXPOQdUIMZC', 'test_user@gmail.com'),
    ('test_user_2' ,'$2a$10$PcSvcvAMh0UjiS8CsiNbzulmxR4ua0g3PDg.eNQGTfwXPOQdUIMZC', 'test_user2@gmail.com');

INSERT INTO user_roles (user_id, role_id)
VALUES
    ((SELECT id FROM users WHERE username = 'test_user'), (SELECT id FROM roles WHERE name = 'ROLE_USER')),
    ((SELECT id FROM users WHERE username = 'test_user_2'), (SELECT id FROM roles WHERE name = 'ROLE_USER'));

INSERT INTO scripts (user_id, name, raw, bytecode, is_bytecode_valid)
VALUES
    ((SELECT id FROM users WHERE username = 'test_user'), 'Pizza Rizza', 'Test Script 1',
        '[30, 11, 32, 14, 34, 17, 31, 17, 10, 35, 0, 13, 35, 0, 14, 35, 0, 11, 35, 0]', true),
    ((SELECT id FROM users WHERE username = 'test_user'), 'Strawberry Sizzle', 'Test Script 2',
        '[33, 5, 0, 35, 0, 12, 35, 0]', true),
    ((SELECT id FROM users WHERE username = 'test_user_2'), 'Banana Bandit', 'Test Script 3',
        '[33, 5, 0, 35, 0, 12, 35, 0]', true),
    ((SELECT id FROM users WHERE username = 'test_user_2'), 'Spaghetti Strangler', 'Test Script 4',
        '[33, 5, 0, 35, 0, 12, 35, 0]', false);