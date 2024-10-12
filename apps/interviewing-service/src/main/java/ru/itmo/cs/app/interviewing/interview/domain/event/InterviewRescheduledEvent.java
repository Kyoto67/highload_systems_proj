package ru.itmo.cs.app.interviewing.interview.domain.event;

import java.time.Instant;

import ru.itmo.cs.app.interviewing.interview.domain.Interview;
import ru.itmo.cs.app.interviewing.interview.domain.value.InterviewId;

public record InterviewRescheduledEvent(
        InterviewId interviewId,
        Instant occurredOn,
        Instant scheduledFor
) implements InterviewEvent {

    public static InterviewRescheduledEvent fromEntity(Interview interview) {
        return new InterviewRescheduledEvent(interview.getId(), interview.getUpdatedAt(), interview.getScheduledFor());
    }

    @Override
    public String eventType() {
        return "InterviewRescheduledEvent";
    }

}
