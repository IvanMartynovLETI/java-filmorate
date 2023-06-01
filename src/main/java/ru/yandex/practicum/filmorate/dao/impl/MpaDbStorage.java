package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.Collection;

@Slf4j
@Component
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Mpa getMpaById(int id) {
        log.info("Request to database for obtaining mpa by id: {} obtained.", id);
        if (id <= 0) {
            throw new IncorrectParameterException("id of MPA is equal to or less than zero.");
        }

        String sqlQuery = "SELECT * FROM mpa WHERE mpa_id = ?";
        SqlRowSet mpaRow = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (!mpaRow.next()) {
            String mpaWarning = "MPA with id: " + id + " doesn't exist.";
            throw new EntityNotFoundException(mpaWarning);
        } else {
            return new Mpa(mpaRow.getInt("mpa_id"), mpaRow.getString("mpa_name"));
        }
    }

    @Override
    public Collection<Mpa> findAllMpa() {
        log.info("Request to database for getting mpa's collection obtained. Now {} mpa present.",
                jdbcTemplate.queryForRowSet("SELECT COUNT(mpa_id) from mpa"));

        String sqlQuery = "SELECT * FROM mpa";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> new Mpa(rs.getInt("mpa_id"),
                rs.getString("mpa_name")));
    }
}