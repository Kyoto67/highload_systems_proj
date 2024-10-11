package ru.itmo.cs.app.interviewing.candidate.domain.event;

import java.time.Instant;

import ru.itmo.cs.app.interviewing.candidate.domain.Candidate;
import ru.itmo.cs.app.interviewing.candidate.domain.value.CandidateId;

public record CandidateScheduledForInterviewEvent(
        CandidateId candidateId,
        Instant occurredOn
) implements CandidateEvent {
    public static CandidateScheduledForInterviewEvent fromEntity(Candidate candidate) {
        return new CandidateScheduledForInterviewEvent(candidate.getId(), candidate.getCreatedAt());
    }
}
