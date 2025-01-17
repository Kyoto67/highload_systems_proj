package ru.ifmo.cs.interviews.domain;

import java.time.Instant;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.ifmo.cs.interviews.domain.value.ScheduleId;
import ru.ifmo.cs.interviews.domain.value.ScheduleStatus;
import ru.ifmo.cs.interviews.infrastructure.pg.entity.PgScheduleEntity;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Schedule {
    private final ScheduleId id;
    private final Instant createdAt;
    @NonNull private Instant updatedAt;
    @NonNull private Instant scheduledFor;
    @NonNull private ScheduleStatus status;

    public static Schedule create(Instant scheduledFor) {
        Instant now = Instant.now();
        return new Schedule(ScheduleId.generate(),
                            now,
                            now,
                            scheduledFor,
                            ScheduleStatus.ACTUAL);
    }

    public static Schedule hydrate(PgScheduleEntity pgScheduleEntity) {
        return new Schedule(ScheduleId.hydrate(pgScheduleEntity.id()),
                            pgScheduleEntity.createdAt().toInstant(),
                            pgScheduleEntity.updatedAt().toInstant(),
                            pgScheduleEntity.scheduledFor().toInstant(),
                            ScheduleStatus.R.fromValue(pgScheduleEntity.status()));
    }

    public void cancel() {
        this.status = ScheduleStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }

    public void fail() {
        this.status = ScheduleStatus.FAILED;
        this.updatedAt = Instant.now();
    }

    public boolean isActual() {
        return status == ScheduleStatus.ACTUAL;
    }

    public boolean isCancelled() {
        return status == ScheduleStatus.CANCELLED;
    }

    public boolean isFailed() {
        return status == ScheduleStatus.FAILED;
    }
}
