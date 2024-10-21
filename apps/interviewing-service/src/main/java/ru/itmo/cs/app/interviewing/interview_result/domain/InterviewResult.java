package ru.itmo.cs.app.interviewing.interview_result.domain;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.itmo.cs.app.interviewing.feedback.domain.value.FeedbackId;
import ru.itmo.cs.app.interviewing.interview_result.domain.event.InterviewResultCreatedEvent;
import ru.itmo.cs.app.interviewing.interview_result.domain.event.InterviewResultEvent;
import ru.itmo.cs.app.interviewing.interview_result.domain.value.InterviewResultId;
import ru.itmo.cs.app.interviewing.interview_result.domain.value.Verdict;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class InterviewResult {
    private final InterviewResultId id;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final FeedbackId feedbackId;
    private final Verdict verdict;
    List<InterviewResultEvent> events = new LinkedList<>();

    public static InterviewResult create(FeedbackId feedbackId, Verdict verdict) {
        Instant now = Instant.now();
        InterviewResult interviewResult = new InterviewResult(InterviewResultId.generate(),
                                                              now,
                                                              now,
                                                              feedbackId,
                                                              verdict);
        interviewResult.events.add(InterviewResultCreatedEvent.fromEntity(interviewResult));
        return interviewResult;
    }

    public static InterviewResult hydrate(
            String id,
            Instant createdAt,
            Instant updatedAt,
            String feedbackId,
            String verdict
    ) {
        return new InterviewResult(
                InterviewResultId.hydrate(id),
                createdAt,
                updatedAt,
                FeedbackId.hydrate(feedbackId),
                Verdict.R.fromValue(verdict)
        );
    }

    public List<InterviewResultEvent> releaseEvents() {
        List<InterviewResultEvent> releasedEvents = events;
        events = new LinkedList<>();
        return releasedEvents;
    }
}
