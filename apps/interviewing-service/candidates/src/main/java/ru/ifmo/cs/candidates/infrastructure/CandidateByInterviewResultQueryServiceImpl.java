package ru.ifmo.cs.candidates.infrastructure;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.ifmo.cs.candidates.application.query.CandidateByInterviewResultQueryService;
import ru.ifmo.cs.candidates.domain.Candidate;
import ru.ifmo.cs.candidates.domain.CandidateRepository;

@Primary
@Service
@AllArgsConstructor
public class CandidateByInterviewResultQueryServiceImpl implements CandidateByInterviewResultQueryService {
    private final CandidateRepository candidateRepository;

    @Override
    public Candidate findFor(String interviewResultId) {
//        InterviewResult interviewResult = interviewResultRepository.findById(interviewResultId);
//        Feedback feedback = feedbackRepository.findById(interviewResult.getFeedbackId());
//        Interview interview = interviewRepository.findById(feedback.getInterviewId());
//        return candidateRepository.findById(interview.getCandidateId());
        return null;
    }

}
