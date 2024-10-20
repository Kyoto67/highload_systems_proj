package ru.itmo.cs.app.interviewing.interview.infrastructure.pg.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.itmo.cs.app.interviewing.interview.infrastructure.pg.entity.PgInterviewEntity;

@Component
public class PgInterviewEntityRowMapper implements RowMapper<PgInterviewEntity> {

    @Override
    public PgInterviewEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new PgInterviewEntity(rs.getString("id"),
                                     rs.getTimestamp("created_at"),
                                     rs.getTimestamp("updated_at"),
                                     rs.getString("interviewer_id"),
                                     rs.getString("candidate_id"));
    }

}
