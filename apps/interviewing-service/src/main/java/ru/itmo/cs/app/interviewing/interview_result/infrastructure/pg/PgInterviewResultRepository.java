package ru.itmo.cs.app.interviewing.interview_result.infrastructure.pg;

import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.ifmo.cs.domain_event.domain.stored_event.StoredDomainEvent;
import ru.ifmo.cs.domain_event.domain.stored_event.StoredDomainEventRepository;
import ru.itmo.cs.app.interviewing.interview_result.domain.InterviewResult;
import ru.itmo.cs.app.interviewing.interview_result.domain.InterviewResultRepository;
import ru.itmo.cs.app.interviewing.interview_result.domain.event.InterviewResultCreatedEvent;
import ru.itmo.cs.app.interviewing.interview_result.domain.event.InterviewResultEvent;
import ru.itmo.cs.app.interviewing.interview_result.domain.value.InterviewResultId;
import ru.itmo.cs.app.interviewing.interview_result.infrastructure.pg.mapper.InterviewResultRowMapper;

@Primary
@Repository
@AllArgsConstructor
public class PgInterviewResultRepository implements InterviewResultRepository {
    private final NamedParameterJdbcOperations jdbcOperations;
    private final InterviewResultRowMapper rowMapper;
    private final StoredDomainEventRepository storedDomainEventRepository;

    private final static String FIND_BY_ID = """
            select * from interview_results
            where id=:id
            """;

    private final static String FIND_ALL = """
            select * from interview_results
            order by created_at
            """;

    private final static String INSERT = """
            INSERT INTO interview_results(
            id,
            created_at,
            updated_at,
            feedback_id,
            verdict
            ) VALUES (
            :id,
            :createdAt,
            :updatedAt,
            :feedbackId,
            :verdict
            )
            """;

    private final static String UPDATE = """
            update interview_results set
            created_at=:createdAt,
            updated_at=:updatedAt,
            feedback_id=:feedbackId,
            verdict=:verdict
            where id=:id
            """;


    @Override
    public InterviewResult findById(InterviewResultId interviewResultId) {
        return jdbcOperations.query(
                        FIND_BY_ID,
                        new MapSqlParameterSource().addValue("id", interviewResultId.value().toString()),
                        rowMapper).stream()
                .findAny()
                .orElseThrow();
    }

    @Override
    @Transactional
    public List<InterviewResult> findAll() {
        return jdbcOperations.query(FIND_ALL, rowMapper);
    }

    @Override
    public void save(InterviewResult interviewResult) {
        List<InterviewResultEvent> releasedEvents = interviewResult.releaseEvents();
        boolean isNew = releasedEvents.stream().anyMatch(event -> event instanceof InterviewResultCreatedEvent);
        if (isNew) {
            insert(interviewResult);
        } else {
            update(interviewResult);
        }

        releasedEvents.stream()
                .map(StoredDomainEvent::of)
                .forEach(storedDomainEventRepository::save);
    }

    private void insert(InterviewResult interviewResult) {
        jdbcOperations.update(
                INSERT,
                new MapSqlParameterSource().addValue("id", interviewResult.getId().value().toString())
                        .addValue("createdAt", interviewResult.getCreatedAt())
                        .addValue("updatedAt", interviewResult.getUpdatedAt())
                        .addValue("feedbackId", interviewResult.getFeedbackId().value().toString())
                        .addValue("verdict", interviewResult.getVerdict().value()));
    }

    private void update(InterviewResult interviewResult) {
        jdbcOperations.update(
                UPDATE,
                new MapSqlParameterSource().addValue("id", interviewResult.getId().value().toString())
                        .addValue("createdAt", interviewResult.getCreatedAt())
                        .addValue("updatedAt", interviewResult.getUpdatedAt())
                        .addValue("feedbackId", interviewResult.getFeedbackId().value().toString())
                        .addValue("verdict", interviewResult.getVerdict().value()));
    }
}
