CREATE TABLE game_sessions_to_users (game_session_id bigint not null references game_sessions on delete cascade,
                                     user_id integer references users on delete cascade,
                                     primary key (game_session_id, user_id));