package ru.ifmo.cs.feedbacks.presentation.consumer;

import java.time.Instant;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.ifmo.cs.consumer.KafkaConsumerProperties;
import ru.ifmo.cs.consumer.KafkaEventsConsumer;
import ru.ifmo.cs.contracts.interviewing_service.interviews.integration_event.InterviewScheduledIntegrationEvent;
import ru.ifmo.cs.domain_event.domain.stored_event.StoredDomainEventRepository;
import ru.ifmo.cs.feedbacks.application.query.FeedbackByInterviewQueryService;
import ru.ifmo.cs.feedbacks.domain.event.FeedbackCreatedEvent;
import ru.ifmo.cs.feedbacks.presentation.integration_event.consumer.InterviewScheduledIntegrationEventConsumer;
import ru.ifmo.cs.integration_tests.AbstractIntegrationTest;
import ru.itmo.cs.command_bus.CommandBus;

@MockBean(classes = {KafkaConsumerProperties.class, KafkaEventsConsumer.class})
class InterviewScheduledIntegrationEventConsumerTest extends AbstractIntegrationTest {
    private InterviewScheduledIntegrationEventConsumer consumer;
    @Autowired
    private CommandBus commandBus;
    @Autowired
    private FeedbackByInterviewQueryService feedbackByInterviewQueryService;
    @Autowired
    private StoredDomainEventRepository storedDomainEventRepository;
    private InterviewScheduledIntegrationEvent stubEvent;

    @BeforeEach
    void setup() {
        consumer = new InterviewScheduledIntegrationEventConsumer(commandBus);
        stubEvent = new InterviewScheduledIntegrationEvent(
                "z",
                Instant.now(),
                "z",
                "z",
                "z",
                Instant.now()
        );
        deliverAllSavedDomainEvents();
    }

    @Test
    void consumerTest() {
        consumer.consume(stubEvent);
        Assertions.assertTrue(feedbackByInterviewQueryService.findByInterviewId("z").isPresent());
        Assertions.assertTrue(storedDomainEventRepository.nextWaitedForDelivery().getEvent() instanceof FeedbackCreatedEvent);
    }

}