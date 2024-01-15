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

INSERT INTO scripts (user_id, name, raw, bytecode, is_bytecode_valid)
VALUES
    ((SELECT id FROM users WHERE username = 'test_user'), 'Pizza Rizza', 'Test Script 1',
    '[30, 11, 32, 14, 34, 17, 31, 17, 10, 35, 0, 13, 35, 0, 14, 35, 0, 11, 35, 0]', true);

INSERT INTO scripts (user_id, name, raw, bytecode, is_bytecode_valid)
VALUES
     ((SELECT id FROM users WHERE username = 'test_user'), 'Strawberry Sizzle', 'Test Script 2',
     '[33, 5, 0, 35, 0, 12, 35, 0]', true);

INSERT INTO maps (name, swarms, file_path, preview_img_url)
VALUES
    ('Fortress', 4, 'ns_fortress4.txt', 'https://i.imgur.com/NXFJ90l.png'),
    ('Arena', 2, 'ns_arena.txt', 'https://i.imgur.com/e3tJLId.png'),
    ('Faceoff', 2, 'ns_faceoff.txt', 'https://i.imgur.com/9cx1CUP.png');