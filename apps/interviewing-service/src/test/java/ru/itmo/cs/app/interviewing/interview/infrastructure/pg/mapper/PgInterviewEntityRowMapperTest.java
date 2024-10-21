package ru.itmo.cs.app.interviewing.interview.infrastructure.pg.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.sql.ResultSet;
import java.sql.Timestamp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.itmo.cs.app.interviewing.interview.infrastructure.pg.entity.PgInterviewEntity;

public class PgInterviewEntityRowMapperTest {

    private PgInterviewEntityRowMapper rowMapper;

    @Mock
    private ResultSet resultSet;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        rowMapper = new PgInterviewEntityRowMapper();
    }

    @Test
    public void testMapRow() throws Exception {
        when(resultSet.getString("id")).thenReturn("123");
        when(resultSet.getTimestamp("created_at")).thenReturn(Timestamp.valueOf("2022-01-01 12:00:00"));
        when(resultSet.getTimestamp("updated_at")).thenReturn(Timestamp.valueOf("2022-01-02 12:00:00"));
        when(resultSet.getString("interviewer_id")).thenReturn("int-456");
        when(resultSet.getString("candidate_id")).thenReturn("cand-789");

        PgInterviewEntity result = rowMapper.mapRow(resultSet, 1);

        assertEquals("123", result.id());
        assertEquals(Timestamp.valueOf("2022-01-01 12:00:00"), result.createdAt());
        assertEquals(Timestamp.valueOf("2022-01-02 12:00:00"), result.updated_at());
        assertEquals("int-456", result.interviewerId());
        assertEquals("cand-789", result.candidateId());
    }

    @Test
    public void testMapRow_withNullValues() throws Exception {
        when(resultSet.getString("id")).thenReturn(null);
        when(resultSet.getTimestamp("created_at")).thenReturn(null);
        when(resultSet.getTimestamp("updated_at")).thenReturn(null);
        when(resultSet.getString("interviewer_id")).thenReturn(null);
        when(resultSet.getString("candidate_id")).thenReturn(null);

        PgInterviewEntity result = rowMapper.mapRow(resultSet, 1);

        assertEquals(null, result.id());
        assertEquals(null, result.createdAt());
        assertEquals(null, result.updated_at());
        assertEquals(null, result.interviewerId());
        assertEquals(null, result.candidateId());
    }
}