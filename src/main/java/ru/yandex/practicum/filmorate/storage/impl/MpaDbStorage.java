package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getAllMpa() {
        return jdbcTemplate.query("SELECT * FROM mpa", (resultSet, rowNum) -> makeMpa(resultSet));
    }

    @Override
    public Optional<Mpa> getMpaById(int id) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM mpa WHERE mpa_id = ?", id);
        if (!sqlRowSet.next()) {
            return Optional.empty();
        }
        Mpa mpa = new Mpa(sqlRowSet.getInt("mpa_id"), sqlRowSet.getString("name"));
        return Optional.of(mpa);
    }

    private Mpa makeMpa(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("mpa_id");
        String name = resultSet.getString("name");
        return new Mpa(id, name);
    }
}
