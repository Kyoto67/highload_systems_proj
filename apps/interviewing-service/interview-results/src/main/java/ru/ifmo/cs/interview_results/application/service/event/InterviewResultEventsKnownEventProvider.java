package ru.ifmo.cs.interview_results.application.service.event;

import java.util.List;

import org.springframework.stereotype.Service;
import ru.ifmo.cs.domain_event.application.service.KnownEventProvider;
import ru.ifmo.cs.domain_event.domain.KnownDomainEvent;
import ru.ifmo.cs.interview_results.domain.event.InterviewResultCreatedEvent;

@Service
public class InterviewResultEventsKnownEventProvider implements KnownEventProvider {

    @Override
    public List<KnownDomainEvent> provide() {
        return List.of(new KnownDomainEvent(InterviewResultCreatedEvent.EVENT_TYPE, InterviewResultCreatedEvent.class));
    }

}
