package ru.ifmo.cs.interviewers.domain;


import java.time.Instant;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.And;
import ru.ifmo.cs.misc.Name;
import ru.ifmo.cs.misc.UserId;
import ru.ifmo.cs.interviewers.domain.event.InterviewerActivatedEvent;
import ru.ifmo.cs.interviewers.domain.event.InterviewerCreatedEvent;
import ru.ifmo.cs.interviewers.domain.event.InterviewerDemotedEvent;
import ru.ifmo.cs.interviewers.domain.event.InterviewerEvent;
import ru.ifmo.cs.interviewers.domain.value.InterviewerStatus;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class InterviewerTest {
    private Interviewer interviewer;

    @BeforeEach
    void setUp() {
        interviewer = Interviewer.create(UserId.of(1488), Name.of("Z V"));
    }

    @Test
    void create_ShouldInitializeInterviewerWithCorrectValues() {
        Assertions.assertThat(interviewer.getId()).isNotNull();
        Assertions.assertThat(interviewer.getCreatedAt()).isNotNull();
        Assertions.assertThat(interviewer.getUpdatedAt()).isNotNull();
        assertTrue(interviewer.getCreatedAt().isBefore(Instant.now()));
        assertTrue(interviewer.getUpdatedAt().isBefore(Instant.now()));
    }

    @Test
    void create_ShouldAddInterviewerCreatedEvent() {
        List<InterviewerEvent> events = interviewer.releaseEvents();
        Assertions.assertThat(events).hasSize(1);
        assertTrue(events.get(0) instanceof InterviewerCreatedEvent);
    }

    @Test
    void releaseEvents_ShouldReturnAllPendingEvents() {
        List<InterviewerEvent> events = interviewer.releaseEvents();
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof InterviewerCreatedEvent);
    }

    @Test
    void releaseEvents_ShouldResetEventsList() {
        assertEquals(1, interviewer.releaseEvents().size());
        assertTrue(interviewer.releaseEvents().isEmpty());
    }

    @Test
    public void testActivateWhenPending() {
        preparePending();

        interviewer.activate();
        Instant oldUpdatedAt = interviewer.getUpdatedAt();
        assertEquals(InterviewerStatus.ACTIVE, interviewer.getInterviewerStatus());
        assertTrue(oldUpdatedAt.isBefore(Instant.now()));
        List<InterviewerEvent> events = interviewer.releaseEvents();
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof InterviewerActivatedEvent);
    }

    @Test
    public void testActivateWhenDemoted() {
        prepareDemoted();

        interviewer.activate();
        Instant oldUpdatedAt = interviewer.getUpdatedAt();

        assertEquals(InterviewerStatus.ACTIVE, interviewer.getInterviewerStatus());
        assertTrue(oldUpdatedAt.isBefore(Instant.now()));
        List<InterviewerEvent> events = interviewer.releaseEvents();
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof InterviewerActivatedEvent);
    }

    @Test
    public void testActivateThrowsException() {
        prepareActivated();
        assertThrows(IllegalStateException.class, interviewer::activate);
    }

    @Test
    public void testDemoteWhenActive() {
        prepareActivated();

        interviewer.demote();
        Instant oldUpdatedAt = interviewer.getUpdatedAt();

        assertEquals(InterviewerStatus.DEMOTED, interviewer.getInterviewerStatus());
        assertTrue(oldUpdatedAt.isBefore(Instant.now()));
        List<InterviewerEvent> events = interviewer.releaseEvents();
        assertEquals(1, events.size());
        assertTrue(events.get(0) instanceof InterviewerDemotedEvent);
    }

    @Test
    public void testDemoteThrowsException() {
        prepareDemoted();
        assertThrows(IllegalStateException.class, interviewer::demote);
    }

    @Test
    public void testDemoteThrowsExceptionWhenStatusIsPendingActivation() {
        assertThrows(IllegalStateException.class, interviewer::demote);
    }

    private void prepareDemoted() {
        interviewer.activate();
        interviewer.demote();
        interviewer.releaseEvents();
    }

    private void prepareActivated() {
        interviewer.activate();
        interviewer.releaseEvents();
    }

    private void preparePending() {
        interviewer.releaseEvents();
    }

    @Test
    void testActivateThrowsException_WhenInterviewerIsActive() {
        prepareActivated();
        assertThrows(IllegalStateException.class, interviewer::activate);
        assertEquals(InterviewerStatus.ACTIVE, interviewer.getInterviewerStatus());
    }

    @Test
    void testCreate_ShouldInitializeInterviewerWithDefaultStatus() {
        Interviewer newInterviewer = Interviewer.create(UserId.of(1234), Name.of("John Doe"));
        assertEquals(InterviewerStatus.PENDING_ACTIVATION, newInterviewer.getInterviewerStatus());
    }

    @Test
    void testActivate_ShouldSetUpdateTimestampCorrectly() {
        preparePending();
        Instant creationTimestamp = interviewer.getCreatedAt();
        interviewer.activate();
        Instant updateTimestamp = interviewer.getUpdatedAt();
        assertTrue(updateTimestamp.isAfter(creationTimestamp));
    }

    @Test
    void testReleaseEvents_ShouldClearEventsList() {
        preparePending();
        interviewer.activate();
        List<InterviewerEvent> events = interviewer.releaseEvents();
        assertFalse(events.isEmpty());
        interviewer.releaseEvents();
        assertTrue(interviewer.releaseEvents().isEmpty());
    }

    @Test
    void testCreate_ShouldInitializeInterviewerWithSpecificStatus() {
        Interviewer newInterviewer = Interviewer.create(UserId.of(5678), Name.of("Jane Doe"), InterviewerStatus.ACTIVE);
        assertEquals(InterviewerStatus.ACTIVE, newInterviewer.getInterviewerStatus());
    }

}