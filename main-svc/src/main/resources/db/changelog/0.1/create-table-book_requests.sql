CREATE TABLE IF NOT EXISTS book_requests(
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
requester_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
book_id BIGINT NOT NULL REFERENCES books(id) ON DELETE SET NULL,
created_on TIMESTAMP NOT NULL
);