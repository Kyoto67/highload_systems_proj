package ru.itmo.cs.app.interviewing.interviewer.infrastructure.pg;

import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.ifmo.cs.domain_event.domain.stored_event.StoredDomainEvent;
import ru.ifmo.cs.domain_event.domain.stored_event.StoredDomainEventRepository;
import ru.itmo.cs.app.interviewing.interviewer.domain.Interviewer;
import ru.itmo.cs.app.interviewing.interviewer.domain.InterviewerRepository;
import ru.itmo.cs.app.interviewing.interviewer.domain.event.InterviewerCreatedEvent;
import ru.itmo.cs.app.interviewing.interviewer.domain.event.InterviewerEvent;
import ru.itmo.cs.app.interviewing.interviewer.domain.value.InterviewerId;
import ru.itmo.cs.app.interviewing.interviewer.infrastructure.pg.mapper.InterviewerRowMapper;

@Primary
@Repository
@AllArgsConstructor
public class PgInterviewerRepository implements InterviewerRepository {
    private final NamedParameterJdbcOperations jdbcOperations;
    private final InterviewerRowMapper rowMapper;
    private final StoredDomainEventRepository storedDomainEventRepository;

    private final static String FIND_BY_ID = """
            select * from interviewers
            where id=:id
            """;

    private final static String FIND_ALL = """
            select * from interviewers
            order by created_at
            """;

    private final static String INSERT = """
            INSERT INTO interviewers(
            id,
            created_at,
            updated_at,
            user_id,
            full_name,
            status
            ) VALUES (
            :id,
            :createdAt,
            :updatedAt,
            :userId,
            :fullName,
            :status
            )
            """;

    private final static String UPDATE = """
            update interviewers set
            created_at=:createdAt,
            updated_at=:updatedAt,
            user_id=:userId,
            full_name=:fullName,
            status=:status
            where id=:id
            """;


    @Override
    public Interviewer findById(InterviewerId id) {
        return jdbcOperations.query(
                        FIND_BY_ID,
                        new MapSqlParameterSource().addValue("id", id.value().toString()),
                        rowMapper).stream()
                .findAny()
                .orElseThrow();
    }

    @Override
    public List<Interviewer> findAll() {
        return jdbcOperations.query(FIND_ALL, rowMapper);
    }

    @Override
    @Transactional
    public void save(Interviewer interviewer) {
        List<InterviewerEvent> releasedEvents = interviewer.releaseEvents();
        boolean isNew = releasedEvents.stream().anyMatch(event -> event instanceof InterviewerCreatedEvent);
        if (isNew) {
            insert(interviewer);
        } else {
            update(interviewer);
        }

        releasedEvents.stream()
                .map(StoredDomainEvent::of)
                .forEach(storedDomainEventRepository::save);
    }

    private void insert(Interviewer interviewer) {
        jdbcOperations.update(
                INSERT,
                new MapSqlParameterSource().addValue("id", interviewer.getId().value().toString())
                        .addValue("createdAt", interviewer.getCreatedAt())
                        .addValue("updatedAt", interviewer.getUpdatedAt())
                        .addValue("userId", interviewer.getUserId())
                        .addValue("fullName", interviewer.getName().fullName())
                        .addValue("status", interviewer.getInterviewerStatus().value()));
    }

    private void update(Interviewer interviewer) {
        jdbcOperations.update(
                UPDATE,
                new MapSqlParameterSource().addValue("id", interviewer.getId().value().toString())
                        .addValue("createdAt", interviewer.getCreatedAt())
                        .addValue("updatedAt", interviewer.getUpdatedAt())
                        .addValue("userId", interviewer.getUserId())
                        .addValue("fullName", interviewer.getName().fullName())
                        .addValue("status", interviewer.getInterviewerStatus().value()));
    }

}
