TRUNCATE TABLE users CASCADE;
TRUNCATE TABLE blacklisted_tokens CASCADE;

INSERT INTO users(id, email, password, date_registered) VALUES
(100, 'user', '$2a$10$6rIpDTj3/hiYiHdnzooaWuSjGTZT8C88aIuRlo9Lph./ZY71fsl5S', '2024-07-02 23:41:10.614686'),
(101, 'admin', '$2a$10$6rIpDTj3/hiYiHdnzooaWuSjGTZT8C88aIuRlo9Lph./ZY71fsl5S', '2024-07-02 23:41:10.614686');

INSERT INTO user_authorities(user_id, authorities)VALUES
(100, 'USER'),
(101, 'ADMIN');

INSERT INTO blacklisted_tokens (id, token, expires_at, blacklisted_at) values
(200, 'eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJvcmlzaGEuZGV2IiwiaWF0IjoxNzIzMzk0Mjk5LCJleHAiOjE3MjM0ODA2OTksInN1YiI6InVzZXIiLCJwcmluY2lwYWwiOiJ1c2VyIiwiY3JlZGVudGlhbHMiOiJbUFJPVEVDVEVEXSIsImF1dGhvcml0aWVzIjpbIlVTRVIiXX0.E-wHrx_7sp2xSloSMoVuVCNY5OdZ6Wh80BomoSAH8XSWSSrD8WB52EInr6Pc6HQKc6ZLzegGY7kDbqxV3ipwtQ',
'2024-07-13T14:02:27.425305100Z', '2024-07-13T14:02:27.434315200');