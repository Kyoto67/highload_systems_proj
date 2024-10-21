package ru.itmo.cs.app.interviewing.interview_result.infrastructure.pg;

import java.util.Optional;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;
import ru.itmo.cs.app.interviewing.feedback.domain.value.FeedbackId;
import ru.itmo.cs.app.interviewing.interview_result.application.query.InterviewResultByFeedbackQueryService;
import ru.itmo.cs.app.interviewing.interview_result.domain.InterviewResult;
import ru.itmo.cs.app.interviewing.interview_result.infrastructure.pg.mapper.InterviewResultRowMapper;

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
    public Optional<InterviewResult> findByFeedbackId(FeedbackId feedbackId) {
        return jdbcOperations.query(
                        QUERY,
                        new MapSqlParameterSource().addValue("feedbackId", feedbackId.value().toString()),
                        rowMapper)
                .stream()
                .findAny();
    }

}
