INSERT INTO mpa(mpa_id, mpa_name) VALUES(1, 'G');
INSERT INTO mpa(mpa_id, mpa_name) VALUES(2, 'PG');
INSERT INTO mpa(mpa_id, mpa_name) VALUES(3, 'PG-13');
INSERT INTO mpa(mpa_id, mpa_name) VALUES(4, 'R');
INSERT INTO mpa(mpa_id, mpa_name) VALUES(5, 'NC-17');

INSERT INTO genre(genre_id, genre_name) VALUES(1, 'Комедия');
INSERT INTO genre(genre_id, genre_name) VALUES(2, 'Драма');
INSERT INTO genre(genre_id, genre_name) VALUES(3, 'Мультфильм');
INSERT INTO genre(genre_id, genre_name) VALUES(4, 'Триллер');
INSERT INTO genre(genre_id, genre_name) VALUES(5, 'Документальный');
INSERT INTO genre(genre_id, genre_name) VALUES(6, 'Боевик');

INSERT INTO users(users_id, name, login, email, birthday) VALUES(2, 'User1', 'login1', 'user1@yandex.ru', '1983-1-11');
INSERT INTO users(users_id, name, login, email, birthday) VALUES(3, 'User2', 'login2', 'user2@yandex.ru', '1989-3-14');
INSERT INTO users(users_id, name, login, email, birthday) VALUES(4, 'User3', 'login3', 'user3@yandex.ru', '1985-8-18');

INSERT INTO film(film_id, film_name, film_description, release_date, duration, mpa_id) VALUES(2, 'film1', 'description1', '2023-4-12', 60, 1);
INSERT INTO film(film_id, film_name, film_description, release_date, duration, mpa_id) VALUES(3, 'film2', 'description2', '2023-3-4', 90, 2);