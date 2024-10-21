package ru.itmo.cs.app.interviewing.interview_result.infrastructure.pg.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.itmo.cs.app.interviewing.interview_result.domain.InterviewResult;

@Component
public class InterviewResultRowMapper implements RowMapper<InterviewResult> {

    @Override
    public InterviewResult mapRow(ResultSet rs, int rowNum) throws SQLException {
        return InterviewResult.hydrate(
                rs.getString("id"),
                rs.getTimestamp("created_at").toInstant(),
                rs.getTimestamp("updated_at").toInstant(),
                rs.getString("feedback_id"),
                rs.getString("verdict")
        );
    }

}
