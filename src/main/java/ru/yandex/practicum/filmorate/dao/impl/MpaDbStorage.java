package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.Collection;

@Slf4j
@Component()
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Mpa getMpaById(int id) {
        log.info("Request to database for obtaining mpa by id: {} obtained.", id);
        Mpa mpa = new Mpa();
        if (id <= 0) {
            throw new IncorrectParameterException("id of MPA is equal to or less than zero.");
        }

        String sqlQuery = "SELECT * FROM mpa WHERE mpa_id = ?";
        SqlRowSet mpaRow = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (!mpaRow.next()) {
            String mpaWarning = "MPA with id: " + id + " doesn't exist.";
            throw new MpaNotFoundException(mpaWarning);
        } else {
            mpa.setId(mpaRow.getInt("mpa_id"));
            mpa.setName(mpaRow.getString("mpa_name"));
        }

        return mpa;
    }

    @Override
    public Collection<Mpa> findAllMpa() {
        log.info("Request to database for getting mpa's collection obtained. Now {} mpa present.",
                jdbcTemplate.queryForRowSet("SELECT COUNT(mpa_id) from mpa"));

        String sqlQuery = "SELECT * FROM mpa";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilledMpa(rs.getInt("mpa_id"),
                rs.getString("mpa_name")));
    }

    private Mpa makeFilledMpa(int id, String name) {
        Mpa mpa = new Mpa();
        mpa.setId(id);
        if (name != null) {
            mpa.setName(name);
        }

        return mpa;
    }
}