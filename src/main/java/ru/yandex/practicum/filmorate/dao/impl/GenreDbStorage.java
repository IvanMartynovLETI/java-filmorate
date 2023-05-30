package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Collection;

@Slf4j
@Component()
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre getGenreById(int id) {
        log.info("Request to database for obtaining genre by id: {} obtained.", id);
        Genre genre = new Genre();
        if (id <= 0) {
            throw new IncorrectParameterException("id of genre is equal to or less than zero.");
        }
        String sqlQuery = "SELECT * FROM genre WHERE genre_id = ?";
        SqlRowSet genreRow = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (!genreRow.next()) {
            String genreWarning = "Genre with id: " + id + " doesn't exist.";
            throw new GenreNotFoundException(genreWarning);
        } else {
            genre.setId(genreRow.getInt("genre_id"));
            genre.setName(genreRow.getString("genre_name"));
        }
        return genre;
    }

    @Override
    public Collection<Genre> findAllGenres() {
        log.info("Request to database for getting genre's collection obtained. Now {} mpa present.",
                jdbcTemplate.queryForRowSet("SELECT COUNT(genre_id) from genre"));
        String sqlQuery = "SELECT * FROM genre";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilledGenre(rs.getInt("genre_id"),
                rs.getString("genre_name")));
    }

    private Genre makeFilledGenre(int id, String name) {
        Genre genre = new Genre();
        genre.setId(id);
        if (name != null) {
            genre.setName(name);
        }
        return genre;
    }
}