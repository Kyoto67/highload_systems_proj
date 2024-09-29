package ru.itmo.cs.app.interviewing.interviewer.infrastructure.in_memory;

import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ifmo.cs.misc.UserId;
import ru.itmo.cs.app.interviewing.interviewer.application.query.InterviewerUniqueIdentifiersQueryService;
import ru.itmo.cs.app.interviewing.interviewer.application.query.dto.InterviewerUniqueIdentifiersDto;
import ru.itmo.cs.app.interviewing.interviewer.domain.InterviewerRepository;
import ru.itmo.cs.app.interviewing.interviewer.domain.value.InterviewerId;

@Service
@AllArgsConstructor
public class InMemoryStubInterviewerUniqueIdentifiersQueryService implements InterviewerUniqueIdentifiersQueryService {
    private final InterviewerRepository interviewerRepository;

    @Override
    public InterviewerUniqueIdentifiersDto findBy(UserId userId) {
        return interviewerRepository.findAll()
                                    .stream()
                                    .filter(interviewer -> interviewer.getUserId().equals(userId))
                                    .findAny()
                                    .map(InterviewerUniqueIdentifiersDto::hydrateFromEntity)
                                    .orElseThrow();
    }

    @Override
    public InterviewerUniqueIdentifiersDto findBy(InterviewerId interviewerId) {
        return interviewerRepository.findAll()
                .stream()
                .filter(interviewer -> interviewer.getInterviewerId().equals(interviewerId))
                .findAny()
                .map(InterviewerUniqueIdentifiersDto::hydrateFromEntity)
                .orElseThrow();
    }

}
