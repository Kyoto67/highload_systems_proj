package ru.itmo.cs.app.interviewing.interviewer.domain.event;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.itmo.cs.app.interviewing.interviewer.domain.Interviewer;
import ru.itmo.cs.app.interviewing.interviewer.domain.value.InterviewerId;

public record InterviewerActivatedEvent(
        @JsonProperty("interviewerId") InterviewerId interviewerId,
        @JsonProperty("occurredOn") Instant occurredOn
) implements InterviewerEvent {
    public static final String EVENT_TYPE = "InterviewerActivated";

    public static InterviewerActivatedEvent fromActivatedEntity(Interviewer interviewer) {
        return new InterviewerActivatedEvent(interviewer.getId(), interviewer.getUpdatedAt());
    }

    @Override
    public String eventType() {
        return EVENT_TYPE;
    }
}
