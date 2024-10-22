package ru.itmo.cs.app.interviewing.candidate.application.command;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ru.itmo.cs.app.interviewing.candidate.domain.Candidate;
import ru.itmo.cs.app.interviewing.candidate.domain.CandidateRepository;
import ru.itmo.cs.app.interviewing.candidate.domain.value.CandidateId;

public class ChageStatusToCancelledCommandHandlerTest {

    private ChageStatusToCancelledCommandHandler handler;

    @Mock
    private CandidateRepository candidateRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new ChageStatusToCancelledCommandHandler(candidateRepository);
    }

    @Test
    public void testHandle_ShouldCancelCandidateStatus() {
        CandidateId candidateId = CandidateId.generate();
        ChageStatusToCancelledCommand command = new ChageStatusToCancelledCommand(candidateId);
        Candidate mockCandidate = mock(Candidate.class);

        when(candidateRepository.findById(candidateId)).thenReturn(mockCandidate);

        handler.handle(command);

        verify(candidateRepository).findById(candidateId);
        verify(mockCandidate).changeStatusToCancelled();
        verify(candidateRepository).save(mockCandidate);
    }
}