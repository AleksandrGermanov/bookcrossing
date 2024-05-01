DROP TABLE if EXISTS users, books, owner_cards, book_requests;

CREATE TABLE IF NOT EXISTS users(
id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
name VARCHAR (250) NOT NULL,
email VARCHAR (250) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS books(
id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
title VARCHAR (250) NOT NULL,
author VARCHAR (500) NOT NULL,
publication_year SMALLINT,
is_available BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS owner_cards(
id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
owner_id BIGINT NOT NULL REFERENCES users(id) ON DELETE SET NULL,
book_id BIGINT NOT NULL REFERENCES books(id) ON DELETE SET NULL,
owned_since TIMESTAMP NOT NULL,
owned_till TIMESTAMP,

CONSTRAINT owned_till_IS_AFTER_owned_since CHECK (owned_till = NULL OR owned_till > owned_since)
);

CREATE TABLE IF NOT EXISTS book_requests(
id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
requester_id BIGINT NOT NULL REFERENCES users(id) ON DELETE SET NULL,
book_id BIGINT NOT NULL REFERENCES books(id) ON DELETE SET NULL,
created_on TIMESTAMP NOT NULL
);