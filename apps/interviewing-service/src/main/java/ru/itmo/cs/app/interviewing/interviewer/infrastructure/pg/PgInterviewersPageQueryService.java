package ru.itmo.cs.app.interviewing.interviewer.infrastructure.pg;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;
import ru.itmo.cs.app.interviewing.interviewer.application.query.InterviewersPageQueryService;
import ru.itmo.cs.app.interviewing.interviewer.application.query.dto.InterviewersPage;
import ru.itmo.cs.app.interviewing.interviewer.domain.Interviewer;
import ru.itmo.cs.app.interviewing.interviewer.infrastructure.pg.mapper.InterviewerRowMapper;

@Primary
@Service
@AllArgsConstructor
public class PgInterviewersPageQueryService implements InterviewersPageQueryService {
    private final NamedParameterJdbcOperations jdbcOperations;
    private final InterviewerRowMapper interviewerRowMapper;

    private static final String QUERY = """
            select *
            from interviewers
            order by created_at desc
            limit :limit
            offset :offset
            """;

    private static final String COUNT_TOTAL = """
            select count(*) as cnt from interviewers
            """;

    @Override
    public InterviewersPage findFor(int page, int size) {
        Long countTotal =
                jdbcOperations.query(COUNT_TOTAL, new PgInterviewersPageQueryService.CountTotalRowMapper())
                              .stream()
                              .findAny()
                              .orElseThrow();

        List<Interviewer> interviewersPage = jdbcOperations.query(
                QUERY,
                new MapSqlParameterSource().addValue("limit", size)
                        .addValue("offset", page * size),
                interviewerRowMapper).stream().toList();
        return new InterviewersPage(interviewersPage, page, size, countTotal);
    }

    private static class CountTotalRowMapper implements RowMapper<Long> {

        @Override
        public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getLong("cnt");
        }
    }

}
