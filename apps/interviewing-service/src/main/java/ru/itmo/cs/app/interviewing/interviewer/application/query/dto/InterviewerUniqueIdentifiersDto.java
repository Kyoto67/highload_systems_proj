package ru.itmo.cs.app.interviewing.interviewer.application.query.dto;

import ru.ifmo.cs.misc.UserId;
import ru.itmo.cs.app.interviewing.interviewer.domain.Interviewer;
import ru.itmo.cs.app.interviewing.interviewer.domain.value.InterviewerId;

public record InterviewerUniqueIdentifiersDto(InterviewerId interviewerId, UserId userId) {
    public static InterviewerUniqueIdentifiersDto hydrateFromEntity(Interviewer interviewer){
        return new InterviewerUniqueIdentifiersDto(interviewer.getInterviewerId(), interviewer.getUserId());
    }
}