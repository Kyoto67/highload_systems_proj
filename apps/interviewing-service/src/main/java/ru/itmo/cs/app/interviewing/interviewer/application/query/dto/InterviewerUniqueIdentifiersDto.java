package ru.itmo.cs.app.interviewing.interviewer.application.query.dto;

import ru.ifmo.cs.misc.UserId;
import ru.itmo.cs.app.interviewing.interviewer.domain.Interviewer;
import ru.itmo.cs.app.interviewing.interviewer.domain.value.InterviewerId;

public record InterviewerUniqueIdentifiersDto(InterviewerId interviewerId, UserId userId) {

    public static InterviewerUniqueIdentifiersDto hydrate(String interviewerId, String userId) {
        return new InterviewerUniqueIdentifiersDto(InterviewerId.hydrate(interviewerId), UserId.of(userId));
    }


    public static InterviewerUniqueIdentifiersDto hydrate(String interviewerId, long userId) {
        return new InterviewerUniqueIdentifiersDto(InterviewerId.hydrate(interviewerId), UserId.of(userId));
    }

    public static InterviewerUniqueIdentifiersDto fromEntity(Interviewer interviewer){
        return new InterviewerUniqueIdentifiersDto(interviewer.getId(), interviewer.getUserId());
    }

}
