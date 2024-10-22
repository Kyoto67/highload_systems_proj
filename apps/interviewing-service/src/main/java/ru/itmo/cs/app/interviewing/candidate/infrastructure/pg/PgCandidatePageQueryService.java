package ru.itmo.cs.app.interviewing.candidate.infrastructure.pg;

import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;
import ru.itmo.cs.app.interviewing.candidate.application.query.CandidatePageQueryService;
import ru.itmo.cs.app.interviewing.candidate.application.query.dto.CandidatePage;
import ru.itmo.cs.app.interviewing.candidate.domain.Candidate;
import ru.itmo.cs.app.interviewing.candidate.infrastructure.pg.mapper.CandidateRowMapper;
import ru.itmo.cs.app.interviewing.libs.page.infrastructure.pg.AbstractPgPageQueryService;

@Primary
@Service
@AllArgsConstructor
public class PgCandidatePageQueryService extends AbstractPgPageQueryService implements CandidatePageQueryService {
    private final NamedParameterJdbcOperations jdbcOperations;
    private final CandidateRowMapper rowMapper;

    private static final String QUERY = """
            select *
            from candidates
            order by created_at desc
            limit :limit
            offset :offset
            """;

    private static final String COUNT_TOTAL = """
            select count(*) as cnt from candidates
            """;

    @Override
    public CandidatePage findFor(int page, int size) {
        Long countTotal =
                jdbcOperations.query(COUNT_TOTAL, new CountTotalRowMapper())
                        .stream()
                        .findAny()
                        .orElseThrow();

        List<Candidate> candidatesPage = jdbcOperations.query(
                        QUERY,
                        new MapSqlParameterSource().addValue("limit", size)
                                .addValue("offset", page * size),
                        rowMapper)
                .stream()
                .toList();
        return new CandidatePage(candidatesPage, page, size, countTotal);
    }
}
