package ru.ifmo.cs.contracts.interviewing_service.interviews.integration_event;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import ru.ifmo.cs.integration_event.IntegrationEvent;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class InterviewScheduledIntegrationEvent implements IntegrationEvent {
    public static final String EVENT_TYPE = "interview_scheduled_integration_event";
    @JsonProperty("event_type")
    String eventType;
    @JsonProperty("deduplication_key")
    String deduplicationKey;
    @JsonProperty("occurred_on")
    Instant occurredOn;
    @JsonProperty("interview_id")
    String interviewId;
    @JsonProperty("interviewer_id")
    String interviewerId;
    @JsonProperty("candidate_id")
    String candidateId;
    @JsonProperty("scheduled_for")
    Instant scheduledFor;

    @JsonCreator
    public InterviewScheduledIntegrationEvent(
            @JsonProperty("deduplication_key") String deduplicationKey,
            @JsonProperty("occurred_on") Instant occurredOn,
            @JsonProperty("interview_id") String interviewId,
            @JsonProperty("interviewer_id") String interviewerId,
            @JsonProperty("candidate_id") String candidateId,
            @JsonProperty("scheduled_for") Instant scheduledFor
    ) {
        this.deduplicationKey = deduplicationKey;
        this.occurredOn = occurredOn;
        this.interviewId = interviewId;
        this.interviewerId = interviewerId;
        this.candidateId = candidateId;
        this.scheduledFor = scheduledFor;
        this.eventType = EVENT_TYPE;
    }
}
