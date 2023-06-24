package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Component
@Primary
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Director addDirector(Director director) {
        log.info("Request to database for director with name '{}' creation obtained.", director.getName());

        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO directors (director_name) values(?)";
        int rowsAffected = jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, director.getName());
            return preparedStatement;
        }, generatedKeyHolder);
        int id = Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();
        director.setId(id);
        return director;
    }

    @Override
    public Director getDirectorById(Long id) {
        Director director;
        log.info("Request to database for getting director by id of '{}' obtained.", id);
        String sqlQuery = "SELECT * FROM directors WHERE director_id = ?";

        SqlRowSet userRow = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (!userRow.next()) {
            String userWarning = "Director with id: " + id + " doesn't exist.";
            throw new EntityNotFoundException(userWarning);
        } else {
            String name = userRow.getString("director_name");
            director = new Director(id, name);
        }
        return director;
    }

    @Override
    public Director modifyDirector(Director director) {
        log.info("Request to database for director with id '{}' update obtained.", director.getId());
        getDirectorById(director.getId());

        String sqlQuery = "UPDATE directors SET DIRECTOR_NAME = ? WHERE director_id = ?";

        jdbcTemplate.update(sqlQuery,
                director.getName(),
                director.getId());

        return getDirectorById(director.getId());
    }

    @Override
    public Director deleteDirector(long id) {
        log.info("Request to database for director with id '{}' deletion obtained.", id);
        Director director = getDirectorById(id);
        jdbcTemplate.update("DELETE FROM directors WHERE director_id = ?", id);
        return director;
    }

    @Override
    public Set<Director> findAllDirectors() {
        log.info("Request to database for all directors collecting obtained.");

        Set<Director> directors = new HashSet();
        String sqlQuery = "SELECT * FROM directors";

        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sqlQuery);

        while (sqlRowSet.next()) {
            long id = sqlRowSet.getLong("director_id");
            String name = sqlRowSet.getString("director_name");
            directors.add(new Director(id, name));
        }
        return directors;
    }
}