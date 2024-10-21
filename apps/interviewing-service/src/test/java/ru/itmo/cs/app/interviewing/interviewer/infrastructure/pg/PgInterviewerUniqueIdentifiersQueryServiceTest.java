package ru.itmo.cs.app.interviewing.interviewer.infrastructure.pg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import java.util.Collections;
import java.util.List;

import ru.ifmo.cs.misc.UserId;
import ru.itmo.cs.app.interviewing.interviewer.application.query.dto.InterviewerUniqueIdentifiersDto;
import ru.itmo.cs.app.interviewing.interviewer.domain.value.InterviewerId;
import ru.itmo.cs.app.interviewing.interviewer.infrastructure.pg.mapper.InterviewerUniqueIdentifiersRowMapper;

public class PgInterviewerUniqueIdentifiersQueryServiceTest {

    private PgInterviewerUniqueIdentifiersQueryService service;

    @Mock
    private NamedParameterJdbcOperations jdbcOperations;

    @Mock
    private InterviewerUniqueIdentifiersRowMapper rowMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new PgInterviewerUniqueIdentifiersQueryService(jdbcOperations, rowMapper);
    }

    @Test
    public void testFindByUserId() {
        UserId userId = UserId.of(123);
        InterviewerUniqueIdentifiersDto mockDto = new InterviewerUniqueIdentifiersDto(InterviewerId.generate(), userId);

        when(jdbcOperations.query(anyString(), any(MapSqlParameterSource.class), eq(rowMapper)))
                .thenReturn(List.of(mockDto));

        InterviewerUniqueIdentifiersDto result = service.findBy(userId);

        assertEquals(mockDto, result);
    }

    @Test
    public void testFindByUserId_NotFound() {
        UserId userId = UserId.of(123);

        when(jdbcOperations.query(anyString(), any(MapSqlParameterSource.class), eq(rowMapper)))
                .thenReturn(Collections.emptyList());

        assertThrows(Exception.class, () -> service.findBy(userId));
    }

    @Test
    public void testFindByInterviewerId() {
        InterviewerId interviewerId = InterviewerId.generate();
        UserId userId = UserId.of(123);
        InterviewerUniqueIdentifiersDto mockDto = new InterviewerUniqueIdentifiersDto(interviewerId, userId);

        when(jdbcOperations.query(anyString(), any(MapSqlParameterSource.class), eq(rowMapper)))
                .thenReturn(List.of(mockDto));

        InterviewerUniqueIdentifiersDto result = service.findBy(interviewerId);

        assertEquals(mockDto, result);
    }

    @Test
    public void testFindByInterviewerId_NotFound() {
        InterviewerId interviewerId = InterviewerId.generate();

        when(jdbcOperations.query(anyString(), any(MapSqlParameterSource.class), eq(rowMapper)))
                .thenReturn(Collections.emptyList());

        assertThrows(Exception.class, () -> service.findBy(interviewerId));
    }
}