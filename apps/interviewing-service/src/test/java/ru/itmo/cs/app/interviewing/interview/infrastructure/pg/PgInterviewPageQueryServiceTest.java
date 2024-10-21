package ru.itmo.cs.app.interviewing.interview.infrastructure.pg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import java.sql.Timestamp;
import java.util.List;

import ru.itmo.cs.app.interviewing.interview.application.query.dto.InterviewPage;
import ru.itmo.cs.app.interviewing.interview.domain.Interview;
import ru.itmo.cs.app.interviewing.interview.domain.Schedule;
import ru.itmo.cs.app.interviewing.interview.infrastructure.pg.entity.PgInterviewEntity;
import ru.itmo.cs.app.interviewing.interview.infrastructure.pg.entity.PgScheduleEntity;
import ru.itmo.cs.app.interviewing.interview.infrastructure.pg.mapper.PgInterviewEntityRowMapper;

public class PgInterviewPageQueryServiceTest {
    private PgInterviewPageQueryService service;
    @Mock
    private NamedParameterJdbcOperations jdbcOperations;
    @Mock
    private PgScheduleEntityDao pgScheduleEntityDao;
    @Mock
    private PgInterviewEntityRowMapper pgInterviewEntityRowMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new PgInterviewPageQueryService(jdbcOperations, pgScheduleEntityDao, pgInterviewEntityRowMapper);
    }

    @Test
    public void testFindFor() {
        int page = 0;
        int size = 10;
        long countTotal = 1L;

        PgInterviewEntity entity = mock(PgInterviewEntity.class);
        when(entity.id()).thenReturn("id123");
        when(entity.createdAt()).thenReturn(Timestamp.valueOf("2022-01-01 12:00:00"));
        when(entity.updated_at()).thenReturn(Timestamp.valueOf("2022-01-02 12:00:00"));
        when(entity.interviewerId()).thenReturn("interviewerId123");
        when(entity.candidateId()).thenReturn("candidateId123");

        when(jdbcOperations.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
                .thenReturn(List.of(entity));

        when(jdbcOperations.query(anyString(), any(RowMapper.class)))
                .thenReturn(List.of(countTotal));

        try (var staticMockInterview = mockStatic(Interview.class);
             var staticMockSchedule = mockStatic(Schedule.class)) {

            Interview mockInterview = mock(Interview.class);
            Schedule mockSchedule = mock(Schedule.class);

            staticMockInterview.when(() -> Interview.hydrate(eq(entity), any())).thenReturn(mockInterview);
            staticMockSchedule.when(() -> Schedule.hydrate(any())).thenReturn(mockSchedule);

            when(pgScheduleEntityDao.findFor(entity)).thenReturn(List.of(mock(PgScheduleEntity.class)));

            InterviewPage result = service.findFor(page, size);

            assertEquals(countTotal, result.totalElements());
            assertEquals(page, result.pageNumber());
            assertEquals(size, result.pageSize());
            assertEquals(1, result.content().size());
            assertEquals(mockInterview, result.content().get(0));
        }
    }
}