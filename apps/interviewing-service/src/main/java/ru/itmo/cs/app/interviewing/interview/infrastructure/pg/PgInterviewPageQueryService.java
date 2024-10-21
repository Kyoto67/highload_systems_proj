package ru.itmo.cs.app.interviewing.interview.infrastructure.pg;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;
import ru.itmo.cs.app.interviewing.interview.application.query.InterviewPageQueryService;
import ru.itmo.cs.app.interviewing.interview.application.query.dto.InterviewPage;
import ru.itmo.cs.app.interviewing.interview.domain.Interview;
import ru.itmo.cs.app.interviewing.interview.infrastructure.pg.entity.PgInterviewEntity;
import ru.itmo.cs.app.interviewing.interview.infrastructure.pg.mapper.PgInterviewEntityRowMapper;

@Primary
@Service
@AllArgsConstructor
public class PgInterviewPageQueryService implements InterviewPageQueryService {
    private final NamedParameterJdbcOperations jdbcOperations;
    private final PgScheduleEntityDao pgScheduleEntityDao;
    private final PgInterviewEntityRowMapper pgInterviewEntityRowMapper;

    private static final String QUERY = """
            select *
            from interviews
            order by created_at desc
            limit :limit
            offset :offset
            """;

    private static final String COUNT_TOTAL = """
            select count(*) as cnt from interviews
            """;

    @Override
    public InterviewPage findFor(int page, int size) {
        Long countTotal = jdbcOperations.query(COUNT_TOTAL, new CountTotalRowMapper()).stream().findAny().orElseThrow();

        List<PgInterviewEntity> pagedPgInterviewEntities = jdbcOperations.query(
                QUERY,
                new MapSqlParameterSource().addValue("limit", size)
                        .addValue("offset", page * size),
                pgInterviewEntityRowMapper).stream().toList();
        List<Interview> content = pagedPgInterviewEntities.stream()
                                                          .map(e -> Interview.hydrate(e, pgScheduleEntityDao.findFor(e)))
                                                          .toList();
        return new InterviewPage(content, page, size, countTotal);
    }

    private static class CountTotalRowMapper implements RowMapper<Long> {

        @Override
        public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getLong("cnt");
        }
    }

}
