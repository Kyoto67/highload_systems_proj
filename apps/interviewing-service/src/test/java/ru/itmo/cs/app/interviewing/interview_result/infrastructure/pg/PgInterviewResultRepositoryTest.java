package ru.itmo.cs.app.interviewing.interview_result.infrastructure.pg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

import ru.ifmo.cs.domain_event.domain.stored_event.StoredDomainEventRepository;
import ru.itmo.cs.app.interviewing.feedback.domain.value.FeedbackId;
import ru.itmo.cs.app.interviewing.interview_result.domain.InterviewResult;
import ru.itmo.cs.app.interviewing.interview_result.domain.event.InterviewResultCreatedEvent;
import ru.itmo.cs.app.interviewing.interview_result.domain.event.InterviewResultEvent;
import ru.itmo.cs.app.interviewing.interview_result.domain.value.InterviewResultId;
import ru.itmo.cs.app.interviewing.interview_result.domain.value.Verdict;
import ru.itmo.cs.app.interviewing.interview_result.infrastructure.pg.mapper.InterviewResultRowMapper;

public class PgInterviewResultRepositoryTest {

    private PgInterviewResultRepository repository;

    @Mock
    private NamedParameterJdbcOperations jdbcOperations;

    @Mock
    private InterviewResultRowMapper rowMapper;

    @Mock
    private StoredDomainEventRepository storedDomainEventRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = new PgInterviewResultRepository(jdbcOperations, rowMapper, storedDomainEventRepository);
    }

    @Test
    public void testFindById() {
        InterviewResultId interviewResultId = InterviewResultId.generate();
        InterviewResult mockInterviewResult = mock(InterviewResult.class);

        when(jdbcOperations.query(anyString(), any(MapSqlParameterSource.class), eq(rowMapper)))
                .thenReturn(List.of(mockInterviewResult));

        InterviewResult result = repository.findById(interviewResultId);

        assertNotNull(result);
        assertEquals(mockInterviewResult, result);
    }

    @Test
    public void testFindById_NotFound() {
        InterviewResultId interviewResultId = InterviewResultId.generate();

        when(jdbcOperations.query(anyString(), any(MapSqlParameterSource.class), eq(rowMapper)))
                .thenReturn(Collections.emptyList());

        assertThrows(Exception.class, () -> repository.findById(interviewResultId));
    }

    @Test
    public void testFindAll() {
        InterviewResult mockInterviewResult = mock(InterviewResult.class);

        when(jdbcOperations.query(anyString(), eq(rowMapper)))
                .thenReturn(List.of(mockInterviewResult));

        List<InterviewResult> results = repository.findAll();

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(mockInterviewResult, results.get(0));
    }

    @Test
    public void testSave_NewInterviewResult() {
        InterviewResult mockInterviewResult = mock(InterviewResult.class);
        InterviewResultCreatedEvent event = mock(InterviewResultCreatedEvent.class);

        when(mockInterviewResult.releaseEvents()).thenReturn(List.of(event));
        when(mockInterviewResult.getId()).thenReturn(InterviewResultId.generate());
        when(mockInterviewResult.getCreatedAt()).thenReturn(Timestamp.valueOf("2023-10-01 00:00:00").toInstant());


        when(mockInterviewResult.getUpdatedAt()).thenReturn(Timestamp.valueOf("2023-10-02 00:00:00").toInstant());
        when(mockInterviewResult.getFeedbackId()).thenReturn(FeedbackId.generate());
        when(mockInterviewResult.getVerdict()).thenReturn(Verdict.HIRE);

        repository.save(mockInterviewResult);

        verify(jdbcOperations).update(anyString(), any(MapSqlParameterSource.class));
        verify(storedDomainEventRepository, times(1)).save(any());
    }

    @Test
    public void testSave_ExistingInterviewResult() {
        InterviewResult mockInterviewResult = mock(InterviewResult.class);

        when(mockInterviewResult.getId()).thenReturn(InterviewResultId.generate());
        when(mockInterviewResult.getCreatedAt()).thenReturn(Timestamp.valueOf("2023-10-01 00:00:00").toInstant());
        when(mockInterviewResult.getUpdatedAt()).thenReturn(Timestamp.valueOf("2023-10-02 00:00:00").toInstant());
        when(mockInterviewResult.getFeedbackId()).thenReturn(FeedbackId.generate());
        when(mockInterviewResult.getVerdict()).thenReturn(Verdict.HIRE);
        when(mockInterviewResult.releaseEvents()).thenReturn(List.of());

        repository.save(mockInterviewResult);
        verify(jdbcOperations).update(anyString(), any(MapSqlParameterSource.class));
        verify(storedDomainEventRepository, times(0)).save(any());
    }
}