package ru.itmo.cs.app.interviewing.interviewer.infrastructure.pg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import java.sql.Timestamp;
import java.util.List;

import ru.ifmo.cs.domain_event.domain.stored_event.StoredDomainEventRepository;
import ru.ifmo.cs.misc.Name;
import ru.ifmo.cs.misc.UserId;
import ru.itmo.cs.app.interviewing.interviewer.domain.Interviewer;
import ru.itmo.cs.app.interviewing.interviewer.domain.value.InterviewerId;
import ru.itmo.cs.app.interviewing.interviewer.domain.event.InterviewerCreatedEvent;
import ru.itmo.cs.app.interviewing.interviewer.domain.event.InterviewerEvent;
import ru.itmo.cs.app.interviewing.interviewer.domain.value.InterviewerStatus;
import ru.itmo.cs.app.interviewing.interviewer.infrastructure.pg.mapper.InterviewerRowMapper;

public class PgInterviewerRepositoryTest {

    private PgInterviewerRepository repository;

    @Mock
    private NamedParameterJdbcOperations jdbcOperations;

    @Mock
    private InterviewerRowMapper rowMapper;

    @Mock
    private StoredDomainEventRepository storedDomainEventRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = new PgInterviewerRepository(jdbcOperations, rowMapper, storedDomainEventRepository);
    }

    @Test
    public void testFindById() {
        InterviewerId id = InterviewerId.generate();
        Interviewer mockInterviewer = mock(Interviewer.class);

        when(jdbcOperations.query(anyString(), any(MapSqlParameterSource.class), eq(rowMapper))).thenReturn(List.of(mockInterviewer));

        Interviewer result = repository.findById(id);

        assertNotNull(result);
        assertEquals(mockInterviewer, result);
    }

    @Test
    public void testFindAll() {
        Interviewer mockInterviewer = mock(Interviewer.class);

        when(jdbcOperations.query(anyString(), eq(rowMapper))).thenReturn(List.of(mockInterviewer));

        List<Interviewer> result = repository.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockInterviewer, result.get(0));
    }

    @Test
    public void testSave_NewInterviewer() {
        Interviewer mockInterviewer = mock(Interviewer.class);
        InterviewerCreatedEvent event = mock(InterviewerCreatedEvent.class);

        when(mockInterviewer.releaseEvents()).thenReturn(List.of(event));
        when(mockInterviewer.getId()).thenReturn(InterviewerId.generate());
        when(mockInterviewer.getCreatedAt()).thenReturn(Timestamp.valueOf("2023-10-01 00:00:00").toInstant());
        when(mockInterviewer.getUpdatedAt()).thenReturn(Timestamp.valueOf("2023-10-02 00:00:00").toInstant());
        when(mockInterviewer.getUserId()).thenReturn(UserId.of(123));
        when(mockInterviewer.getName()).thenReturn(Name.of("John Doe"));
        when(mockInterviewer.getInterviewerStatus()).thenReturn(InterviewerStatus.ACTIVE);

        repository.save(mockInterviewer);

        verify(jdbcOperations).update(anyString(), any(MapSqlParameterSource.class));
        verify(storedDomainEventRepository, times(1)).save(any());
    }

    @Test
    public void testSave_ExistingInterviewer() {
        Interviewer mockInterviewer = mock(Interviewer.class);
        InterviewerEvent event = mock(InterviewerCreatedEvent.class);

        when(mockInterviewer.releaseEvents()).thenReturn(List.of(event));
        when(mockInterviewer.getId()).thenReturn(InterviewerId.generate());
        when(mockInterviewer.getCreatedAt()).thenReturn(Timestamp.valueOf("2023-10-01 00:00:00").toInstant());
        when(mockInterviewer.getUpdatedAt()).thenReturn(Timestamp.valueOf("2023-10-02 00:00:00").toInstant());
        when(mockInterviewer.getUserId()).thenReturn(UserId.of(123));
        when(mockInterviewer.getName()).thenReturn(Name.of("John Doe"));
        when(mockInterviewer.getInterviewerStatus()).thenReturn(InterviewerStatus.ACTIVE);
        repository.save(mockInterviewer);
        reset(storedDomainEventRepository);
        reset(jdbcOperations);
        when(mockInterviewer.releaseEvents()).thenReturn(List.of());



        repository.save(mockInterviewer);
        verify(jdbcOperations).update(anyString(), any(MapSqlParameterSource.class));
        verify(storedDomainEventRepository, times(0)).save(any());
    }
}
