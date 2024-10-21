package ru.itmo.cs.app.interviewing.interviewer.infrastructure.pg.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import java.util.List;

import ru.itmo.cs.app.interviewing.interviewer.application.query.dto.InterviewersPage;
import ru.itmo.cs.app.interviewing.interviewer.domain.Interviewer;
import ru.itmo.cs.app.interviewing.interviewer.infrastructure.pg.PgInterviewersPageQueryService;

public class PgInterviewersPageQueryServiceTest {

    private PgInterviewersPageQueryService service;

    @Mock
    private NamedParameterJdbcOperations jdbcOperations;

    @Mock
    private InterviewerRowMapper interviewerRowMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new PgInterviewersPageQueryService(jdbcOperations, interviewerRowMapper);
    }

    @Test
    public void testFindFor() {
        int page = 0;
        int size = 10;
        long countTotal = 100L;

        Interviewer mockInterviewer = mock(Interviewer.class);

        when(jdbcOperations.query(anyString(), any(RowMapper.class)))
                .thenReturn(List.of(countTotal));

        when(jdbcOperations.query(anyString(), any(MapSqlParameterSource.class), eq(interviewerRowMapper)))
                .thenReturn(List.of(mockInterviewer));

        InterviewersPage result = service.findFor(page, size);

        assertEquals(countTotal, result.totalElements());
        assertEquals(page, result.pageNumber());
        assertEquals(size, result.pageSize());
        assertEquals(1, result.content().size());
        assertEquals(mockInterviewer, result.content().get(0));
    }
}