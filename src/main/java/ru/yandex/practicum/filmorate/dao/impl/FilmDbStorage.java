package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.Validator;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component()
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final Validator validator;
    private static final int FILMS_COUNT_BY_DEFAULT = 10;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, Validator validator) {
        this.jdbcTemplate = jdbcTemplate;
        this.validator = validator;

    }

    @Override
    public Film addFilm(Film film) {
        log.info("Request to database for film with name '{}' creation obtained.", film.getName());
        Film checkedMovie = validator.validateFilmInDataBase(film,jdbcTemplate, true);
        String sqlQuery;

        sqlQuery = "INSERT INTO film(film_name, film_description, release_date, duration) " +
                "VALUES(?, ?, ?, ?)";

        jdbcTemplate.update(sqlQuery,
                checkedMovie.getName(),
                checkedMovie.getDescription(),
                java.sql.Date.valueOf(checkedMovie.getReleaseDate()),
                checkedMovie.getDuration().toMinutes());

        if(checkedMovie.getMpa() != null) {
            sqlQuery = "UPDATE film SET mpa_id = ? WHERE film_id = ?";
            jdbcTemplate.update(sqlQuery, checkedMovie.getMpa().getId(), checkedMovie.getId());
        }

        if(!checkedMovie.getGenres().isEmpty()) {
            for(Genre genre: checkedMovie.getGenres()) {
                jdbcTemplate.update("INSERT INTO film_genre(film_id, genre_id) VALUES(?, ?)", checkedMovie.getId(),
                        genre.getId());
            }
        }

        return getFilmById(checkedMovie.getId());
    }

    @Override
    public Film modifyFilm(Film film) {
        log.info("Request to database for film with id '{}' update obtained.", film.getId());
        Film checkedMovie = validator.validateFilmInDataBase(film,jdbcTemplate, false);

        String sqlQuery = "SELECT film_name FROM film WHERE film_id = ?";
        SqlRowSet filmRow = jdbcTemplate.queryForRowSet(sqlQuery, checkedMovie.getId());

        if(!filmRow.next()) {
            String filmWarning = "Film with id: " + checkedMovie.getId() + " doesn't exist.";
            throw new FilmNotFoundException(filmWarning);
        }

        sqlQuery = "UPDATE film SET film_name = ?, film_description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "WHERE film_id= ?";
        jdbcTemplate.update(sqlQuery,
                checkedMovie.getName(),
                checkedMovie.getDescription(),
                checkedMovie.getReleaseDate(),
                checkedMovie.getDuration().toMinutes(),
                checkedMovie.getMpa().getId(),
                checkedMovie.getId());

        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", checkedMovie.getId());

        if(!checkedMovie.getGenres().isEmpty()) {
            for(Genre genre: checkedMovie.getGenres()) {
                jdbcTemplate.update("INSERT INTO film_genre(film_id, genre_id) VALUES(?, ?)", checkedMovie.getId(),
                        genre.getId());
            }
        }

        return getFilmById(checkedMovie.getId());
    }

    @Override
    public Film deleteFilm(Film film) {
        log.info("Request to database for film with id '{}' deletion obtained.", film.getId());
        Film checkedMovie = validator.validateFilmInDataBase(film,jdbcTemplate, false);
        String sqlQuery = "SELECT film_name FROM film WHERE film_id = ?";
        SqlRowSet filmRow = jdbcTemplate.queryForRowSet(sqlQuery, checkedMovie.getId());

        if(!filmRow.next()) {
            String filmWarning = "Film with id: " + checkedMovie.getId() + " doesn't exist.";
            throw new FilmNotFoundException(filmWarning);
        }

        jdbcTemplate.update("DELETE FROM film_like WHERE film_id = ?", checkedMovie.getId());
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", checkedMovie.getId());
        jdbcTemplate.update("DELETE FROM film WHERE film_id = ?", checkedMovie.getId());

        return checkedMovie;
    }

    @Override
    public Film getFilmById(Long id) {
        log.info("Request to database for obtaining film by id: {} obtained.", id);
        Film film = new Film();
        String sqlQuery = "SELECT * FROM film JOIN mpa ON film.mpa_id = mpa.mpa_id WHERE film_id = ?";

        SqlRowSet filmRow = jdbcTemplate.queryForRowSet(sqlQuery, id);

        if(!filmRow.next()) {
            String filmWarning = "Film with id: " + id + " doesn't exist.";
            throw new FilmNotFoundException(filmWarning);
        } else {
            film.setId(filmRow.getLong("film_id"));
            film.setLikesToFilm(new HashSet<>(jdbcTemplate.queryForList("SELECT user_id FROM film_like WHERE " +
                    "film_id = ?", Long.class, id)));
            film.setName(filmRow.getString("film_name"));
            film.setDescription(filmRow.getString("film_description"));
            film.setReleaseDate(Objects.requireNonNull(filmRow.getDate("release_date")).toLocalDate());
            film.setDuration(Duration.ofMinutes(filmRow.getInt("duration")));

            Mpa mpa = new Mpa();
            mpa.setId(filmRow.getInt("mpa_id"));
            mpa.setName(filmRow.getString("mpa_name"));
            film.setMpa(mpa);

            sqlQuery = "SELECT * FROM film_genre WHERE film_id = ?";
            filmRow = jdbcTemplate.queryForRowSet(sqlQuery, id);

            List<Genre> genres = new ArrayList<>();
            if(!filmRow.next()) {
                film.setGenres(new HashSet<>(genres));
            } else {
                sqlQuery = "SELECT genre.genre_id, genre_name FROM genre JOIN film_genre ON genre.genre_id = " +
                        "film_genre.genre_id WHERE film_id = ?";
                genres = jdbcTemplate.query(sqlQuery, (rs, rowNum) ->
                        makeFilledGenre(rs.getInt("genre.genre_id"),
                                rs.getString("genre.genre_name")), id);
                film.setGenres(new HashSet<>(genres));
            }
        }
        return film;
    }

    @Override
    public Collection<Film> findAllMovies() {
        log.info("Request to database for getting film's collection obtained.");
        String sqlQueryOuter = "SELECT * FROM film JOIN mpa ON film.mpa_id = mpa.mpa_id";
        String sqlQueryInner = "SELECT genre.genre_id, genre_name FROM genre JOIN film_genre ON genre.genre_id = " +
                "film_genre.genre_id WHERE film_id = ?";

        return jdbcTemplate.query(sqlQueryOuter, (rs, rowNum) -> makeFilledFilm(rs.getLong("film_id"),
                new HashSet<>(jdbcTemplate.queryForList("SELECT user_id FROM film_like WHERE " +
                        "film_id = ?", Long.class, rs.getLong("film_id"))),
                rs.getString("film_name"), rs.getString("film_description"),
                rs.getString("release_date"),
                rs.getInt("duration"), rs.getInt("mpa_id"),
                rs.getString("mpa_name"), new HashSet<>(jdbcTemplate.query(sqlQueryInner,
                        (resSet, rowNumber) -> makeFilledGenre(resSet.getInt("genre.genre_id"),
                                resSet.getString("genre.genre_name")),
                        rs.getInt("film.film_id")))));
    }

    @Override
    public Film addLikeToFilm(Long id, Long userId, UserStorage userStorage) {
        if (id == null) {
            throw new IncorrectParameterException("'id' parameter equals to null.");
        }
        if (userId == null) {
            throw new IncorrectParameterException("'userId' parameter equals to null.");
        }

        String sqlQuery = "SELECT * from film WHERE film_id = ?";
        SqlRowSet filmRow = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if(!filmRow.next()) {
            String filmWarning = "Film with id: " + id + " doesn't exist.";
            throw new FilmNotFoundException(filmWarning);
        }

        sqlQuery = "SELECT * from users WHERE users_id = ?";
        SqlRowSet userRow = jdbcTemplate.queryForRowSet(sqlQuery, userId);
        if(!userRow.next()) {
            String userWarning = "User with id: " + userId + " doesn't exist.";
            throw new UserNotFoundException(userWarning);
        }

        sqlQuery = "INSERT INTO film_like(film_id, user_id) VALUES(?, ?)";
        jdbcTemplate.update(sqlQuery, id, userId);

        log.info("Request to database: user with id: {} set like to film with id: {}", userId, id);

        return getFilmById(id);
    }

    @Override
    public Film deleteLikeFromFilm(Long filmId, Long userId, UserStorage userStorage) {
        if (filmId == null) {
            throw new IncorrectParameterException("'id' parameter equals to null.");
        }
        if (userId == null) {
            throw new IncorrectParameterException("'userId' parameter equals to null.");
        }

        String sqlQuery = "SELECT * from film WHERE film_id = ?";
        SqlRowSet filmRow = jdbcTemplate.queryForRowSet(sqlQuery, filmId);
        if(!filmRow.next()) {
            String filmWarning = "Film with id: " + filmId + " doesn't exist.";
            throw new FilmNotFoundException(filmWarning);
        }

        sqlQuery = "SELECT * from users WHERE users_id = ?";
        SqlRowSet userRow = jdbcTemplate.queryForRowSet(sqlQuery, userId);
        if(!userRow.next()) {
            String userWarning = "User with id: " + userId + " doesn't exist.";
            throw new UserNotFoundException(userWarning);
        }

        log.info("Request to database: user with id: {} deletes like from film with id: {}", userId, filmId);

        sqlQuery = "DELETE FROM film_like WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, filmId, userId);

        return getFilmById(filmId);
    }

    @Override
    public List<Film> getTopFilms(Integer count) {
        if (count == null) {
            count = FILMS_COUNT_BY_DEFAULT;
        }
        if (count < 0) {
            throw new IncorrectParameterException("'count' parameter less than zero.");
        }

        List<Film> popularFilms;
        List<Film> unpopularFilms;
        List<Film> resultList;

        String sqlQuery = "SELECT * FROM film_like GROUP BY film_id ORDER BY COUNT(user_id) DESC LIMIT " + count;
        popularFilms = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> getFilmById(rs.getLong("film_id")));

        sqlQuery = "SELECT * FROM film";
        unpopularFilms = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> getFilmById(rs.getLong("film_id")));

        popularFilms.addAll(unpopularFilms);

        if(popularFilms.size() <= count) {
            resultList = popularFilms;
        } else {
            resultList = new ArrayList<>();

            for(int i = 0; i < count; i++) {
                resultList.add(popularFilms.get(i));
            }
        }

        return resultList;
    }

    private Film makeFilledFilm(Long id, Set<Long> likesToFilm, String name, String description,
                                String releaseDate, Integer duration, Integer mpaId, String mpaName, Set<Genre> genres)
    {
        Film film = new Film();
        Mpa mpa = new Mpa();
        film.setId(id);
        if(!likesToFilm.isEmpty()){
            film.setLikesToFilm(likesToFilm);
        }
        if(name != null) {
            film.setName(name);
        }
        if(description != null) {
            film.setDescription(description);
        }
        if(releaseDate != null) {
            film.setReleaseDate(LocalDate.parse(releaseDate));
        }
        if(duration != null) {
            film.setDuration(Duration.ofMinutes(duration));
        }
        if(mpaId != null) {
            mpa.setId(mpaId);
        }
        if(mpaName != null) {
            mpa.setName(mpaName);
        }
        film.setMpa(mpa);
        if(genres != null) {
            film.setGenres(genres);
        }

        return film;
    }

    private Genre makeFilledGenre(Integer id, String name) {
        Genre genre = new Genre();
        genre.setId(id);
        if(name != null) {
            genre.setName(name);
        }

        return genre;
    }
}