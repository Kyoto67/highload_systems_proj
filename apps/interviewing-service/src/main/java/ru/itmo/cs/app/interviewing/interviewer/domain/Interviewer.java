package ru.itmo.cs.app.interviewing.interviewer.domain;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.ifmo.cs.misc.UserId;
import ru.ifmo.cs.misc.Name;
import ru.itmo.cs.app.interviewing.interviewer.domain.event.InterviewerActivatedEvent;
import ru.itmo.cs.app.interviewing.interviewer.domain.event.InterviewerCreatedEvent;
import ru.itmo.cs.app.interviewing.interviewer.domain.event.InterviewerDemotedEvent;
import ru.itmo.cs.app.interviewing.interviewer.domain.event.InterviewerEvent;
import ru.itmo.cs.app.interviewing.interviewer.domain.value.InterviewerId;
import ru.itmo.cs.app.interviewing.interviewer.domain.value.InterviewerStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Interviewer {
    private final InterviewerId id;
    private final UserId userId;
    private final Name name;
    private final Instant createdAt;
    @NonNull private Instant updatedAt;
    @NonNull private InterviewerStatus interviewerStatus;
    @Getter(AccessLevel.NONE)
    private List<InterviewerEvent> events = new LinkedList<>();

    public static Interviewer create(UserId userId, Name name, InterviewerStatus status) {
        Instant now = Instant.now();
        Interviewer createdEntity = new Interviewer(InterviewerId.generate(),
                                                    userId,
                                                    name,
                                                    now,
                                                    now,
                                                    status);
        createdEntity.events.add(InterviewerCreatedEvent.fromEntity(createdEntity));
        return createdEntity;
    }

    public static Interviewer create(UserId userId, Name name) {
        Instant now = Instant.now();
        Interviewer createdEntity = new Interviewer(InterviewerId.generate(),
                                                    userId,
                                                    name,
                                                    now,
                                                    now,
                                                    InterviewerStatus.PENDING_ACTIVATION);
        createdEntity.events.add(InterviewerCreatedEvent.fromEntity(createdEntity));
        return createdEntity;
    }

    public static Interviewer hydrate(
            String id,
            long userId,
            String fullName,
            Instant createdAt,
            Instant updatedAt,
            String status
    ) {
        return new Interviewer(
                InterviewerId.hydrate(id),
                UserId.of(userId),
                Name.of(fullName),
                createdAt,
                updatedAt,
                InterviewerStatus.R.fromValue(status)
        );
    }

    public void activate() {
        validateCanBeActivated();
        interviewerStatus = InterviewerStatus.ACTIVE;
        updatedAt = Instant.now();
        events.add(InterviewerActivatedEvent.fromActivatedEntity(this));
    }

    public void demote() {
        validateCanBeDemoted();
        interviewerStatus = InterviewerStatus.DEMOTED;
        updatedAt = Instant.now();
        events.add(InterviewerDemotedEvent.fromDemotedEntity(this));
    }

    private void validateCanBeDemoted() {
        if (interviewerStatus != InterviewerStatus.ACTIVE) {
            throw new IllegalStateException("Interviewer " + id + "can not be demoted!");
        }
    }

    private void validateCanBeActivated() {
        if (interviewerStatus != InterviewerStatus.PENDING_ACTIVATION
                && interviewerStatus != InterviewerStatus.DEMOTED) {
            throw new IllegalStateException("Interviewer " + id + "can not be activated!");
        }
    }

    public List<InterviewerEvent> releaseEvents() {
        List<InterviewerEvent> releasedEvents = events;
        events = new LinkedList<>();
        return releasedEvents;
    }

}
