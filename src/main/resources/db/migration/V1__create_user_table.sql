CREATE TABLE poem_user (
    id SERIAL,
    first_name TEXT,
    last_name TEXT,
    email TEXT NOT NULL UNIQUE
);