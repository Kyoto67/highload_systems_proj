package ru.itmo.cs.app.interviewing.interviewer.domain.event;

import java.time.Instant;

import lombok.Value;
import ru.itmo.cs.app.interviewing.interviewer.domain.Interviewer;
import ru.itmo.cs.app.interviewing.interviewer.domain.value.InterviewerId;

@Value
public class InterviewerCreatedEvent implements InterviewerEvent {
    InterviewerId interviewerId;
    Instant occurredOn;

    public static InterviewerCreatedEvent fromCreatedEntity(Interviewer interviewer) {
        return new InterviewerCreatedEvent(interviewer.getInterviewerId(), interviewer.getCreatedAt());
    }
}
