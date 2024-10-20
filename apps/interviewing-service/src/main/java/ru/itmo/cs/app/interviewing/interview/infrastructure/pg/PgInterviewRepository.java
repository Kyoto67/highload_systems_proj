package ru.itmo.cs.app.interviewing.interview.infrastructure.pg;

import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Component;
import ru.ifmo.cs.domain_event.domain.stored_event.StoredDomainEvent;
import ru.ifmo.cs.domain_event.domain.stored_event.StoredDomainEventRepository;
import ru.itmo.cs.app.interviewing.interview.domain.Interview;
import ru.itmo.cs.app.interviewing.interview.domain.InterviewRepository;
import ru.itmo.cs.app.interviewing.interview.domain.event.InterviewEvent;
import ru.itmo.cs.app.interviewing.interview.domain.event.InterviewScheduledEvent;
import ru.itmo.cs.app.interviewing.interview.domain.value.InterviewId;
import ru.itmo.cs.app.interviewing.interview.infrastructure.pg.entity.PgInterviewEntity;
import ru.itmo.cs.app.interviewing.interview.infrastructure.pg.entity.PgScheduleEntity;
import ru.itmo.cs.app.interviewing.interview.infrastructure.pg.mapper.PgInterviewEntityRowMapper;

@Component
@AllArgsConstructor
public class PgInterviewRepository implements InterviewRepository {
    private final NamedParameterJdbcOperations jdbcOperations;
    private final PgScheduleEntityDao pgScheduleEntityDao;
    private final PgInterviewEntityRowMapper pgInterviewEntityRowMapper;
    private final StoredDomainEventRepository storedDomainEventRepository;

    private final static String FIND_BY_ID = """
            select * from interviews
            where id=:id
            """;

    private final static String FIND_ALL = """
            select * from interviews
            order by created_at
            """;

    private final static String INSERT = """
            INSERT INTO interviews(
            id,
            created_at,
            updated_at,
            interviewer_id,
            candidate_id
            ) VALUES (
            :id,
            :createdAt,
            :updatedAt,
            :interviewerId,
            :candidateId
            )
            """;

    private final static String UPDATE = """
            update interviews set
            created_at=:createdAt,
            updated_at=:updatedAt,
            interviewer_id=:interviewerId,
            candidate_id=:candidateId
            where id=:id
            """;

    @Override
    public Interview findById(InterviewId id) {
        PgInterviewEntity pgInterviewEntity = jdbcOperations.query(
                        FIND_BY_ID,
                        new MapSqlParameterSource().addValue("id", id.value().toString()),
                        pgInterviewEntityRowMapper
                ).stream()
                .findAny()
                .orElseThrow();
        return Interview.hydrate(pgInterviewEntity, pgScheduleEntityDao.findFor(pgInterviewEntity));
    }

    @Override
    public List<Interview> findAll() {
        List<PgInterviewEntity> entities = jdbcOperations.query(
                FIND_ALL,
                new MapSqlParameterSource(),
                pgInterviewEntityRowMapper);

        return entities.stream().map(e -> Interview.hydrate(e, pgScheduleEntityDao.findFor(e))).toList();
    }

    @Override
    public void save(Interview interview) {
        List<InterviewEvent> releasedEvents = interview.releaseEvents();
        boolean isNew = releasedEvents.stream().anyMatch(event -> event instanceof InterviewScheduledEvent);
        if (isNew) {
            insert(interview);
        } else {
            update(interview);
        }

        releasedEvents.stream()
                .map(StoredDomainEvent::of)
                .forEach(storedDomainEventRepository::save);
    }

    private void insert(Interview interview) {
        jdbcOperations.update(
                INSERT,
                new MapSqlParameterSource().addValue("id", interview.getId().toString())
                                           .addValue("createdAt", interview.getCreatedAt())
                                           .addValue("updatedAt", interview.getUpdatedAt())
                                           .addValue("interviewerId", interview.getInterviewerId())
                                           .addValue("candidateId", interview.getCandidateId()));
    }

    private void update(Interview interview) {
        jdbcOperations.update(
                UPDATE,
                new MapSqlParameterSource().addValue("id", interview.getId().toString())
                                           .addValue("createdAt", interview.getCreatedAt())
                                           .addValue("updatedAt", interview.getUpdatedAt())
                                           .addValue("interviewerId", interview.getInterviewerId())
                                           .addValue("candidateId", interview.getCandidateId()));
        interview.getSchedules().stream().map(PgScheduleEntity::from).forEach(pgScheduleEntityDao::save);
    }
}
