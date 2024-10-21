package ru.itmo.cs.app.interviewing.interview_result.infrastructure.in_memory;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.itmo.cs.app.interviewing.interview_result.application.query.InterviewResultPageQueryService;
import ru.itmo.cs.app.interviewing.interview_result.application.query.dto.InterviewResultPage;
import ru.itmo.cs.app.interviewing.interview_result.domain.InterviewResult;
import ru.itmo.cs.app.interviewing.interview_result.domain.InterviewResultRepository;

@Service
public class InMemoryStubInterviewResultPageQueryService implements InterviewResultPageQueryService {
    private final InterviewResultRepository interviewResultRepository;

    public InMemoryStubInterviewResultPageQueryService(
            @Qualifier("inMemoryStubInterviewResultRepository") InterviewResultRepository interviewResultRepository
    ) {
        this.interviewResultRepository = interviewResultRepository;
    }

    @Override
    public InterviewResultPage findFor(int page, int size) {
        List<InterviewResult> allInterviewResults = interviewResultRepository.findAll();
        int from = page * size;
        int to = Math.min(from + size, allInterviewResults.size());
        List<InterviewResult> content = allInterviewResults.subList(from, to);
        return new InterviewResultPage(content,
                                       page,
                                       size,
                                       allInterviewResults.size());
    }
}
