package ru.ifmo.cs.interview_results.infrastructure.pg;

import java.util.Optional;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;
import ru.ifmo.cs.interview_results.application.query.InterviewResultByFeedbackQueryService;
import ru.ifmo.cs.interview_results.domain.InterviewResult;
import ru.ifmo.cs.interview_results.infrastructure.pg.mapper.InterviewResultRowMapper;

@Primary
@Service
@AllArgsConstructor
public class PgInterviewResultByFeedbackQueryService implements InterviewResultByFeedbackQueryService {
    private final NamedParameterJdbcOperations jdbcOperations;
    private final InterviewResultRowMapper rowMapper;

    private final static String QUERY = """
            select * from interview_results
            where feedback_id=:feedbackId
            """;

    @Override
    public Optional<InterviewResult> findByFeedbackId(String feedbackId) {
        return jdbcOperations.query(
                        QUERY,
                        new MapSqlParameterSource().addValue("feedbackId", feedbackId),
                        rowMapper)
                .stream()
                .findAny();
    }

}
