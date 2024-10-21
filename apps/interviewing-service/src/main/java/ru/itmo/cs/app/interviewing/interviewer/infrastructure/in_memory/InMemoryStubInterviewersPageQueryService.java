package ru.itmo.cs.app.interviewing.interviewer.infrastructure.in_memory;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.itmo.cs.app.interviewing.interviewer.application.query.InterviewersPageQueryService;
import ru.itmo.cs.app.interviewing.interviewer.application.query.dto.InterviewersPage;
import ru.itmo.cs.app.interviewing.interviewer.domain.Interviewer;
import ru.itmo.cs.app.interviewing.interviewer.domain.InterviewerRepository;

@Service
public class InMemoryStubInterviewersPageQueryService implements InterviewersPageQueryService {
    private final InterviewerRepository interviewerRepository;

    public InMemoryStubInterviewersPageQueryService(
            @Qualifier("inMemoryStubInterviewerRepository") InterviewerRepository interviewerRepository
    ) {
        this.interviewerRepository = interviewerRepository;
    }

    @Override
    public InterviewersPage findFor(int page, int size) {
        List<Interviewer> allInterviewers = interviewerRepository.findAll();
        int from = page * size;
        int to = Math.min(from + size, allInterviewers.size());
        List<Interviewer> content = allInterviewers.subList(from, to);
        return new InterviewersPage(content,
                                    page,
                                    size,
                                    allInterviewers.size());
    }
}
