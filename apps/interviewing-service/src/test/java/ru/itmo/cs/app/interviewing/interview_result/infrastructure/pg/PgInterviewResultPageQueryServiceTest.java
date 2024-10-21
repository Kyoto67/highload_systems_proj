package ru.itmo.cs.app.interviewing.interview_result.infrastructure.pg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import java.util.List;

import ru.itmo.cs.app.interviewing.interview_result.application.query.dto.InterviewResultPage;
import ru.itmo.cs.app.interviewing.interview_result.domain.InterviewResult;
import ru.itmo.cs.app.interviewing.interview_result.infrastructure.pg.mapper.InterviewResultRowMapper;

public class PgInterviewResultPageQueryServiceTest {

    private PgInterviewResultPageQueryService service;

    @Mock
    private NamedParameterJdbcOperations jdbcOperations;

    @Mock
    private InterviewResultRowMapper rowMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new PgInterviewResultPageQueryService(jdbcOperations, rowMapper);
    }

    @Test
    public void testFindFor() {
        int page = 0;
        int size = 10;
        long countTotal = 100L;
        InterviewResult mockInterviewResult = Mockito.mock(InterviewResult.class);  // Подставьте ваш метод создания объекта

        when(jdbcOperations.query(anyString(), any(RowMapper.class)))
                .thenReturn(List.of(countTotal));

        when(jdbcOperations.query(eq("""
            select *
            from interview_results
            order by created_at desc
            limit :limit
            offset :offset
            """), any(MapSqlParameterSource.class), eq(rowMapper)))
                .thenReturn(List.of(mockInterviewResult));

        InterviewResultPage result = service.findFor(page, size);

        assertEquals(countTotal, result.totalElements());
        assertEquals(page, result.pageNumber());
        assertEquals(size, result.pageSize());
        assertEquals(1, result.content().size());
        assertEquals(mockInterviewResult, result.content().get(0));
    }
}