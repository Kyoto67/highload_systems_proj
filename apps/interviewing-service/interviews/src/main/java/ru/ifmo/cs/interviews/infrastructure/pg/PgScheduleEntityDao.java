package ru.ifmo.cs.interviews.infrastructure.pg;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Component;
import ru.ifmo.cs.interviews.infrastructure.pg.entity.PgInterviewEntity;
import ru.ifmo.cs.interviews.infrastructure.pg.entity.PgScheduleEntity;
import ru.ifmo.cs.interviews.infrastructure.pg.mapper.PgScheduleEntityRowMapper;

@Component
@AllArgsConstructor
class PgScheduleEntityDao {
    private final NamedParameterJdbcOperations jdbcOperations;
    private final PgScheduleEntityRowMapper pgScheduleEntityRowMapper;

    private final static String FIND_BY_INTERVIEW_ID = """
            select * from schedules
            where interview_id=:interviewId
            """;

    private final static String INSERT_ON_CONFLICT_UPDATE = """
            insert into schedules (id, created_at, updated_at, scheduled_for, status, interview_id)
            values
                (:id, :createdAt, :updatedAt, :scheduledFor, :status, :interviewId)
            ON CONFLICT (id) DO UPDATE SET
            created_at = :createdAt,
            updated_at = :updatedAt,
            scheduled_for = :scheduledFor,
            status = :status
            """;

    List<PgScheduleEntity> findFor(PgInterviewEntity pgInterviewEntity) {
        return jdbcOperations.query(
                FIND_BY_INTERVIEW_ID,
                new MapSqlParameterSource().addValue("interviewId", UUID.fromString(pgInterviewEntity.id())),
                pgScheduleEntityRowMapper
        );
    }

    void save(PgScheduleEntity schedule) {
        jdbcOperations.update(
                INSERT_ON_CONFLICT_UPDATE, new MapSqlParameterSource()
                        .addValue("id", UUID.fromString(schedule.id()))
                        .addValue("createdAt", schedule.createdAt())
                        .addValue("updatedAt", schedule.updatedAt())
                        .addValue("scheduledFor", schedule.scheduledFor())
                        .addValue("status", schedule.status())
                        .addValue("interviewId", UUID.fromString(schedule.interviewId())));
    }

}
