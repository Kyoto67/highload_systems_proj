package ru.itmo.cs.app.interviewing.interviewer.domain.event;

import java.time.Instant;

import ru.itmo.cs.app.interviewing.interviewer.domain.Interviewer;
import ru.itmo.cs.app.interviewing.interviewer.domain.value.InterviewerId;

public record InterviewerCreatedEvent(InterviewerId interviewerId, Instant occurredOn) implements InterviewerEvent {
    public static final String EVENT_TYPE = "InterviewerCreated";

    public static InterviewerCreatedEvent fromEntity(Interviewer interviewer) {
        return new InterviewerCreatedEvent(interviewer.getId(), interviewer.getCreatedAt());
    }

    @Override
    public String eventType() {
        return EVENT_TYPE;
    }
}
