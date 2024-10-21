package ru.itmo.cs.app.interviewing.interview_result.infrastructure.pg;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;
import ru.itmo.cs.app.interviewing.interview_result.application.query.InterviewResultPageQueryService;
import ru.itmo.cs.app.interviewing.interview_result.application.query.dto.InterviewResultPage;
import ru.itmo.cs.app.interviewing.interview_result.domain.InterviewResult;
import ru.itmo.cs.app.interviewing.interview_result.infrastructure.pg.mapper.InterviewResultRowMapper;

@Primary
@Service
@AllArgsConstructor
public class PgInterviewResultPageQueryService implements InterviewResultPageQueryService {
    private final NamedParameterJdbcOperations jdbcOperations;
    private final InterviewResultRowMapper rowMapper;

    private static final String QUERY = """
            select *
            from interview_results
            order by created_at desc
            limit :limit
            offset :offset
            """;

    private static final String COUNT_TOTAL = """
            select count(*) as cnt from interview_results
            """;

    @Override
    public InterviewResultPage findFor(int page, int size) {
        Long countTotal =
                jdbcOperations.query(COUNT_TOTAL, new PgInterviewResultPageQueryService.CountTotalRowMapper())
                        .stream()
                        .findAny()
                        .orElseThrow();

        List<InterviewResult> interviewResultsPage = jdbcOperations.query(
                        QUERY,
                        new MapSqlParameterSource().addValue("limit", size)
                                .addValue("offset", page * size),
                        rowMapper)
                .stream()
                .toList();
        return new InterviewResultPage(interviewResultsPage, page, size, countTotal);
    }

    private static class CountTotalRowMapper implements RowMapper<Long> {

        @Override
        public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getLong("cnt");
        }

    }
}
