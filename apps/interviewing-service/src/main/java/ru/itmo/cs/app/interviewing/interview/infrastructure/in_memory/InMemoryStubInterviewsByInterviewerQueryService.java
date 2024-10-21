package ru.itmo.cs.app.interviewing.interview.infrastructure.in_memory;

import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.itmo.cs.app.interviewing.interview.application.query.InterviewsByInterviewerQueryService;
import ru.itmo.cs.app.interviewing.interview.application.query.dto.InterviewsByInterviewerDto;
import ru.itmo.cs.app.interviewing.interview.domain.Interview;
import ru.itmo.cs.app.interviewing.interview.domain.InterviewRepository;
import ru.itmo.cs.app.interviewing.interviewer.domain.value.InterviewerId;

@Service
public class InMemoryStubInterviewsByInterviewerQueryService implements InterviewsByInterviewerQueryService {
    private final InterviewRepository interviewRepository;

    public InMemoryStubInterviewsByInterviewerQueryService(
            @Qualifier("inMemoryStubInterviewRepository") InterviewRepository interviewRepository
    ) {
        this.interviewRepository = interviewRepository;
    }

    @Override
    public InterviewsByInterviewerDto findBy(InterviewerId interviewerId) {
        List<Interview> interviewList = interviewRepository.findAll()
                                                           .stream()
                                                           .filter(interview -> interview.getInterviewerId()
                                                                                         .equals(interviewerId))
                                                           .toList();
        return new InterviewsByInterviewerDto(interviewerId, interviewList);
    }

}
