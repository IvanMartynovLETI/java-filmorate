DROP TABLE IF EXISTS film_like;
DROP TABLE IF EXISTS user_friends_status;
DROP TABLE IF EXISTS film_genre;
DROP TABLE IF EXISTS genre;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS film;
DROP TABLE IF EXISTS mpa;

CREATE TABLE IF NOT EXISTS mpa (
    mpa_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    mpa_name varchar(255)
);
   
CREATE TABLE IF NOT EXISTS film (
    film_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_name varchar(255),
    film_description varchar(255),
    release_date date,
    duration int,
    mpa_id INTEGER REFERENCES mpa(mpa_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS users (
    users_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(255) NOT NULL,
    email varchar(255) NOT NULL,
    login varchar(255) NOT NULL,
    birthday date NOT NULL
);
 
CREATE TABLE IF NOT EXISTS film_like (
    film_like_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id BIGINT NOT NULL REFERENCES film(film_id),
    user_id BIGINT NOT NULL REFERENCES users(users_id)
);

CREATE TABLE IF NOT EXISTS genre (
    genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    genre_name varchar(255)
);

CREATE TABLE IF NOT EXISTS film_genre (
    film_genre_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id BIGINT NOT NULL REFERENCES film(film_id),
    genre_id INTEGER NOT NULL REFERENCES genre(genre_id)
);

CREATE TABLE IF NOT EXISTS user_friends_status (
    user_friends_status_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(users_id),
    friend_id BIGINT NOT NULL REFERENCES users(users_id),
    status_of_friendship varchar(20),
    CONSTRAINT status_of_friendship_constraint CHECK (status_of_friendship = 'Confirmed'
    OR status_of_friendship = 'Unconfirmed')
);