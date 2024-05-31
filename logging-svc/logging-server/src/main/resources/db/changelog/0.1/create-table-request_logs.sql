CREATE TABLE IF NOT EXISTS request_logs
(
id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
method               VARCHAR(16),
path                 VARCHAR(255),
ip                   VARCHAR(64),
parameter_map        VARCHAR(512),
serialized_body      VARCHAR(2048),
created_on           TIMESTAMP NOT NULL
);