package ru.itmo.cs.app.interviewing.interview.domain;

import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.itmo.cs.app.interviewing.candidate.domain.value.CandidateId;
import ru.itmo.cs.app.interviewing.interview.domain.event.InterviewCancelledEvent;
import ru.itmo.cs.app.interviewing.interview.domain.event.InterviewEvent;
import ru.itmo.cs.app.interviewing.interview.domain.event.InterviewRescheduledEvent;
import ru.itmo.cs.app.interviewing.interview.domain.event.InterviewScheduledEvent;
import ru.itmo.cs.app.interviewing.interview.domain.specification.InterviewIsCancelledSpecification;
import ru.itmo.cs.app.interviewing.interview.domain.specification.InterviewIsWaitingForConductSpecification;
import ru.itmo.cs.app.interviewing.interview.domain.value.InterviewId;
import ru.itmo.cs.app.interviewing.interview.infrastructure.pg.entity.PgInterviewEntity;
import ru.itmo.cs.app.interviewing.interview.infrastructure.pg.entity.PgScheduleEntity;
import ru.itmo.cs.app.interviewing.interviewer.domain.value.InterviewerId;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Interview {
    private final InterviewId id;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final InterviewerId interviewerId;
    private final CandidateId candidateId;
    private final List<Schedule> schedules;
    @Getter(AccessLevel.NONE)
    private List<InterviewEvent> events = new LinkedList<>();

    public static Interview create(InterviewerId interviewerId, CandidateId candidateId, Instant scheduledFor) {
        Instant now = Instant.now();
        Interview createdInterview = new Interview(InterviewId.generate(),
                                                   now,
                                                   now,
                                                   interviewerId,
                                                   candidateId,
                                                   new LinkedList<>(List.of(Schedule.create(scheduledFor))));
        createdInterview.events.add(InterviewScheduledEvent.fromEntity(createdInterview));
        return createdInterview;
    }

    public static Interview hydrate(
            PgInterviewEntity pgInterviewEntity,
            List<PgScheduleEntity> pgScheduleEntities
    ) {
        return new Interview(InterviewId.hydrate(pgInterviewEntity.interviewerId()),
                             pgInterviewEntity.createdAt().toInstant(),
                             pgInterviewEntity.updated_at().toInstant(),
                             InterviewerId.hydrate(pgInterviewEntity.interviewerId()),
                             CandidateId.hydrate(pgInterviewEntity.candidateId()),
                             pgScheduleEntities.stream().map(Schedule::hydrate).toList());
    }

    public Instant getScheduledFor() {
        return getActualSchedule().orElseThrow().getScheduledFor();
    }

    public Optional<Instant> getScheduledForO() {
        return getActualSchedule().map(Schedule::getScheduledFor);
    }

    public void reschedule(Instant newScheduledFor) {
        if (!InterviewIsWaitingForConductSpecification.isSatisfiedBy(this)) {
            throw new IllegalStateException("Interview is not waiting for conduct");
        }
        getActualSchedule().orElseThrow().cancel();
        this.schedules.add(Schedule.create(newScheduledFor));
        this.events.add(InterviewRescheduledEvent.fromEntity(this));
    }

    public void cancel() {
        if (InterviewIsCancelledSpecification.isSatisfiedBy(this)) {
            throw new IllegalStateException("Interview is already cancelled");
        }
        getActualSchedule().orElseThrow().cancel();
        this.events.add(InterviewCancelledEvent.fromEntity(this));
    }

    private Optional<Schedule> getActualSchedule() {
        return schedules.stream()
                        .filter(Schedule::isActual)
                        .findAny();
    }

    public List<InterviewEvent> releaseEvents() {
        List<InterviewEvent> releasedEvents = events;
        events = new LinkedList<>();
        return releasedEvents;
    }
}
