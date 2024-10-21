package ru.itmo.cs.app.interviewing.interview.infrastructure.pg.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.sql.ResultSet;
import java.sql.Timestamp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.itmo.cs.app.interviewing.interview.infrastructure.pg.entity.PgScheduleEntity;

public class PgScheduleEntityRowMapperTest {

    private PgScheduleEntityRowMapper rowMapper;

    @Mock
    private ResultSet resultSet;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        rowMapper = new PgScheduleEntityRowMapper();
    }

    @Test
    public void testMapRow() throws Exception {
        when(resultSet.getString("id")).thenReturn("123");
        when(resultSet.getTimestamp("created_at")).thenReturn(Timestamp.valueOf("2022-01-01 12:00:00"));
        when(resultSet.getTimestamp("updated_at")).thenReturn(Timestamp.valueOf("2022-01-02 12:00:00"));
        when(resultSet.getTimestamp("scheduled_for")).thenReturn(Timestamp.valueOf("2022-01-03 14:00:00"));
        when(resultSet.getString("status")).thenReturn("Scheduled");

        PgScheduleEntity result = rowMapper.mapRow(resultSet, 1);

        assertEquals("123", result.id());
        assertEquals(Timestamp.valueOf("2022-01-01 12:00:00"), result.createdAt());
        assertEquals(Timestamp.valueOf("2022-01-02 12:00:00"), result.updatedAt());
        assertEquals(Timestamp.valueOf("2022-01-03 14:00:00"), result.scheduledFor());
        assertEquals("Scheduled", result.status());
    }

    @Test
    public void testMapRow_withNullValues() throws Exception {
        when(resultSet.getString("id")).thenReturn(null);
        when(resultSet.getTimestamp("created_at")).thenReturn(null);
        when(resultSet.getTimestamp("updated_at")).thenReturn(null);
        when(resultSet.getTimestamp("scheduled_for")).thenReturn(null);
        when(resultSet.getString("status")).thenReturn(null);

        PgScheduleEntity result = rowMapper.mapRow(resultSet, 1);

        assertEquals(null, result.id());
        assertEquals(null, result.createdAt());
        assertEquals(null, result.updatedAt());
        assertEquals(null, result.scheduledFor());
        assertEquals(null, result.status());
    }
}