CREATE TABLE IF NOT EXISTS books(
id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
title VARCHAR (250) NOT NULL,
author VARCHAR (500) NOT NULL,
publication_year SMALLINT,
is_available BOOLEAN NOT NULL
);