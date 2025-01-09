package ru.ifmo.cs.interview_results.infrastructure.pg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import java.util.Collections;
import java.util.Optional;

import ru.ifmo.cs.interview_results.domain.InterviewResult;
import ru.ifmo.cs.interview_results.infrastructure.pg.mapper.InterviewResultRowMapper;

public class PgInterviewResultByFeedbackQueryServiceTest {

    private PgInterviewResultByFeedbackQueryService service;

    @Mock
    private NamedParameterJdbcOperations jdbcOperations;

    @Mock
    private InterviewResultRowMapper rowMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new PgInterviewResultByFeedbackQueryService(jdbcOperations, rowMapper);
    }

    @Test
    public void testFindByFeedbackId_Found() {
        InterviewResult mockInterviewResult = Mockito.mock(InterviewResult.class); // Вы можете использовать актуальный конструктор или мок объекта

        when(jdbcOperations.query(anyString(), any(MapSqlParameterSource.class), Mockito.eq(rowMapper)))
                .thenReturn(Collections.singletonList(mockInterviewResult));

        Optional<InterviewResult> result = service.findByFeedbackId("feedbackId");

        assertTrue(result.isPresent());
        assertEquals(mockInterviewResult, result.get());
    }

    @Test
    public void testFindByFeedbackId_NotFound() {
        when(jdbcOperations.query(anyString(), any(MapSqlParameterSource.class), Mockito.eq(rowMapper)))
                .thenReturn(Collections.emptyList());

        Optional<InterviewResult> result = service.findByFeedbackId("feedbackId");

        assertFalse(result.isPresent());
    }
}