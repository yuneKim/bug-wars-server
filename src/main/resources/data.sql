INSERT INTO roles (name)
VALUES ('ROLE_ADMIN'), ('ROLE_USER')
ON CONFLICT (name) DO NOTHING;

--username: test_user
--password: sausages
INSERT INTO users (username, password, email)
VALUES
    ('test_user' ,'$2a$10$PcSvcvAMh0UjiS8CsiNbzulmxR4ua0g3PDg.eNQGTfwXPOQdUIMZC', 'test_user@gmail.com')
ON CONFLICT (username) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
VALUES
    ((SELECT id FROM users WHERE username = 'test_user'), (SELECT id FROM roles WHERE name = 'ROLE_USER'))
ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO script (user_id, name, script_string, bytecode_string, is_bytecode_valid)
VALUES
    ((SELECT id FROM users WHERE username = 'test_user'), 'Pizza Rizza', ':START att isFood eat',
    '3a 53 54 41 52 54 20 61 74 74 20 69 73 46 6f 6f 64 20 65 61 74', true);

    INSERT INTO script (user_id, name, script_string, bytecode_string, is_bytecode_valid)
    VALUES
        ((SELECT id FROM users WHERE username = 'test_user'), 'Strawberry Sizzle', ':START att isFood run',
        '3a 53 54 41 52 54 20 61 74 74 20 69 73 46 6f 6f 64 20 65 51 72', true);