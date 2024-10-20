package ru.itmo.cs.app.interviewing.interview.infrastructure.pg.entity;

import java.sql.Time;
import java.sql.Timestamp;

import ru.itmo.cs.app.interviewing.interview.domain.Schedule;

public record PgScheduleEntity(
        String id,
        Timestamp createdAt,
        Timestamp updatedAt,
        Timestamp scheduledFor,
        String status
) {
    public static PgScheduleEntity from(Schedule schedule) {
        return new PgScheduleEntity(schedule.getId().value().toString(),
                                    Timestamp.from(schedule.getCreatedAt()),
                                    Timestamp.from(schedule.getUpdatedAt()),
                                    Timestamp.from(schedule.getScheduledFor()),
                                    schedule.getStatus().value());
    }
}
