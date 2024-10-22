package ru.itmo.cs.app.interviewing.interviewer.infrastructure.in_memory;

import java.util.LinkedList;
import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.ifmo.cs.domain_event.domain.stored_event.StoredDomainEvent;
import ru.ifmo.cs.domain_event.domain.stored_event.StoredDomainEventRepository;
import ru.itmo.cs.app.interviewing.interviewer.domain.Interviewer;
import ru.itmo.cs.app.interviewing.interviewer.domain.InterviewerRepository;
import ru.itmo.cs.app.interviewing.interviewer.domain.event.InterviewerCreatedEvent;
import ru.itmo.cs.app.interviewing.interviewer.domain.event.InterviewerEvent;
import ru.itmo.cs.app.interviewing.interviewer.domain.value.InterviewerId;

@Repository
@AllArgsConstructor
public class InMemoryStubInterviewerRepository implements InterviewerRepository {
    private final StoredDomainEventRepository inMemoryStubStoredDomainEventRepository;
    private final List<Interviewer> stubRepository = new LinkedList<>();

    @Override
    public Interviewer findById(InterviewerId id) {
        return stubRepository.stream()
                             .filter(entity -> entity.getId().equals(id))
                             .findAny()
                             .orElseThrow();
    }

    @Override
    public List<Interviewer> findAll() {
        return List.copyOf(stubRepository);
    }

    @Override
    public void save(Interviewer interviewer) {
        List<InterviewerEvent> releasedEvents = interviewer.releaseEvents();
        boolean isNew = releasedEvents.stream().anyMatch(event -> event instanceof InterviewerCreatedEvent);
        if (isNew) {
            insert(interviewer);
        } else {
            update(interviewer);
        }
        releasedEvents.stream()
                      .map(StoredDomainEvent::of)
                      .forEach(inMemoryStubStoredDomainEventRepository::save);
    }

    private void insert(Interviewer interviewer) {
        stubRepository.add(interviewer);
    }

    private void update(Interviewer interviewer) {
        boolean entityForUpdateExists = stubRepository.removeIf(entity -> entity.getId().equals(interviewer.getId()));
        if (!entityForUpdateExists) {
            throw new IllegalStateException();
        }
        stubRepository.add(interviewer);
    }
}
