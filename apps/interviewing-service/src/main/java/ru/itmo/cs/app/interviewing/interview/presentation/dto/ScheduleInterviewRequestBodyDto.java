package ru.itmo.cs.app.interviewing.interview.presentation.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.itmo.cs.app.interviewing.candidate.domain.value.CandidateId;
import ru.itmo.cs.app.interviewing.interviewer.domain.value.InterviewerId;

public record ScheduleInterviewRequestBodyDto(
        @JsonProperty("interviewer_id") InterviewerId interviewerId,
        @JsonProperty("candidate_id") CandidateId candidateId,
        @JsonProperty("scheduled_time") Instant scheduledTime
) {}
