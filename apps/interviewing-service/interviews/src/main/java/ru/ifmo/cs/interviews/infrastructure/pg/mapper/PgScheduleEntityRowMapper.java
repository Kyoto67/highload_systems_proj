package ru.ifmo.cs.interviews.infrastructure.pg.mapper;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.ifmo.cs.interviews.infrastructure.pg.entity.PgScheduleEntity;

@Component
public class PgScheduleEntityRowMapper implements RowMapper<PgScheduleEntity> {

    @Override
    public PgScheduleEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new PgScheduleEntity(rs.getString("id"),
                rs.getTimestamp("created_at"),
                rs.getTimestamp("updated_at"),
                rs.getTimestamp("scheduled_for"),
                rs.getString("status"),
                rs.getString("interview_id"));
    }

}
