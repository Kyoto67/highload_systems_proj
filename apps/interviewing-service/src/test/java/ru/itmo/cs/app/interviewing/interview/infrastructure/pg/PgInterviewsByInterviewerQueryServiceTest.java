package ru.itmo.cs.app.interviewing.interview.infrastructure.pg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import java.util.Collections;
import java.util.List;

import ru.itmo.cs.app.interviewing.interview.application.query.dto.InterviewsByInterviewerDto;
import ru.itmo.cs.app.interviewing.interview.domain.Interview;
import ru.itmo.cs.app.interviewing.interview.infrastructure.pg.entity.PgInterviewEntity;
import ru.itmo.cs.app.interviewing.interview.infrastructure.pg.entity.PgScheduleEntity;
import ru.itmo.cs.app.interviewing.interview.infrastructure.pg.mapper.PgInterviewEntityRowMapper;
import ru.itmo.cs.app.interviewing.interviewer.domain.value.InterviewerId;

public class PgInterviewsByInterviewerQueryServiceTest {

    private PgInterviewsByInterviewerQueryService service;

    @Mock
    private NamedParameterJdbcOperations jdbcOperations;

    @Mock
    private PgInterviewEntityRowMapper pgInterviewEntityRowMapper;

    @Mock
    private PgScheduleEntityDao pgScheduleEntityDao;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new PgInterviewsByInterviewerQueryService(jdbcOperations, pgInterviewEntityRowMapper, pgScheduleEntityDao);
    }

    @Test
    public void testFindBy() {
        InterviewerId interviewerId = InterviewerId.generate();
        PgInterviewEntity mockEntity = mock(PgInterviewEntity.class);
        List<PgScheduleEntity> scheduleEntities = Collections.emptyList();

        List<PgInterviewEntity> mockEntities = List.of(mockEntity);
        when(jdbcOperations.query(anyString(), any(MapSqlParameterSource.class), eq(pgInterviewEntityRowMapper)))
                .thenReturn(mockEntities);
        when(pgScheduleEntityDao.findFor(mockEntity)).thenReturn(scheduleEntities);

        Interview mockInterview = mock(Interview.class);
        try (var staticMock = mockStatic(Interview.class)) {
            staticMock.when(() -> Interview.hydrate(mockEntity, scheduleEntities)).thenReturn(mockInterview);

            InterviewsByInterviewerDto result = service.findBy(interviewerId);

            assertEquals(interviewerId, result.interviewerId());
            assertEquals(1, result.interviews().size());
            assertEquals(mockInterview, result.interviews().get(0));
        }
    }
}