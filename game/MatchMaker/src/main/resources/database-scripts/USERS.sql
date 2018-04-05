CREATE TABLE users (id serial primary key,
                    login varchar unique not null,
                    rank integer not null default 0);