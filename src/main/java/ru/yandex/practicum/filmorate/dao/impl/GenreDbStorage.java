package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.*;

@Slf4j
@Component
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre getGenreById(int id) {
        log.info("Request to database for obtaining genre by id: {} obtained.", id);
        if (id <= 0) {
            throw new IncorrectParameterException("id of genre is equal to or less than zero.");
        }
        String sqlQuery = "SELECT * FROM genre WHERE genre_id = ?";
        SqlRowSet genreRow = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (!genreRow.next()) {
            String genreWarning = "Genre with id: " + id + " doesn't exist.";
            throw new EntityNotFoundException(genreWarning);
        } else {
            return new Genre(genreRow.getInt("genre_id"), genreRow.getString("genre_name"));
        }
    }

    @Override
    public Collection<Genre> findAllGenres() {
        log.info("Request to database for getting genre's collection obtained. Now {} mpa present.",
                jdbcTemplate.queryForRowSet("SELECT COUNT(genre_id) from genre"));
        String sqlQuery = "SELECT * FROM genre";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> new Genre(rs.getInt("genre_id"),
                rs.getString("genre_name")));
    }

    @Override
    public void findGenres(List<Film> filmList) {
        Map<Long, Set<Genre>> map = new HashMap<>();
        StringJoiner stringJoiner = new StringJoiner(", ");
        for (Film film : filmList) {
            stringJoiner.add(film.getId().toString());
            map.put(film.getId(), new TreeSet<>());
        }
        String sql = "SELECT * "
                + "FROM film_genre AS f "
                + "LEFT OUTER JOIN genre AS g ON f.genre_id = g.genre_id "
                + "WHERE film_id IN (" + stringJoiner + ");";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql);
        while (rs.next()) {
            Long id = rs.getLong("film_id");
            System.out.println(rs.getInt("genre_id")+" "+rs.getString("genre_name")+" "+id);
            Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("genre_name"));
            map.get(id).add(genre);
        }
        for (Film film : filmList) {
            film.setGenres(map.get(film.getId()));
        }
    }
}