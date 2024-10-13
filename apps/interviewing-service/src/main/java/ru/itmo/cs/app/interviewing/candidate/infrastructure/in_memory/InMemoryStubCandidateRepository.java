package ru.itmo.cs.app.interviewing.candidate.infrastructure.in_memory;

import java.util.LinkedList;
import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.ifmo.cs.domain_event.domain.stored_event.StoredDomainEvent;
import ru.ifmo.cs.domain_event.domain.stored_event.StoredDomainEventRepository;
import ru.itmo.cs.app.interviewing.candidate.domain.Candidate;
import ru.itmo.cs.app.interviewing.candidate.domain.CandidateRepository;
import ru.itmo.cs.app.interviewing.candidate.domain.event.CandidateCreatedEvent;
import ru.itmo.cs.app.interviewing.candidate.domain.event.CandidateEvent;
import ru.itmo.cs.app.interviewing.candidate.domain.value.CandidateId;

@Repository
@AllArgsConstructor
public class InMemoryStubCandidateRepository implements CandidateRepository {
    private final StoredDomainEventRepository storedDomainEventRepository;
    private final List<Candidate> stubRepository = new LinkedList<>();

    @Override
    public Candidate findById(CandidateId id) {
        return stubRepository.stream()
                             .filter(candidate -> candidate.getId().equals(id))
                             .findAny()
                             .orElseThrow();
    }

    @Override
    public List<Candidate> findAll() {
        return List.copyOf(stubRepository);
    }

    @Override
    public void save(Candidate candidate) {
        List<CandidateEvent> releasedEvents = candidate.releaseEvents();
        boolean isNew = releasedEvents.stream().anyMatch(e -> e instanceof CandidateCreatedEvent);

        if (isNew) {
            insert(candidate);
        } else {
            update(candidate);
        }

        releasedEvents.stream()
                .map(StoredDomainEvent::of)
                .forEach(storedDomainEventRepository::save);
    }

    private void insert(Candidate candidate) {
        stubRepository.add(candidate);
    }

    private void update(Candidate candidate) {
        boolean entityForUpdateExists = stubRepository.removeIf(entity -> entity.getId().equals(candidate.getId()));
        if (!entityForUpdateExists) {
            throw new IllegalStateException();
        }
        stubRepository.add(candidate);
    }
}