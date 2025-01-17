package ru.ifmo.cs.consumer;

import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ifmo.cs.integration_event.IntegrationEvent;
import ru.ifmo.cs.integration_event.event_delivery.IntegrationEventFanoutDelivererService;
import ru.ifmo.cs.integration_event.event_delivery.KnownIntegrationEventTypeResolver;

@Slf4j
@Service
@AllArgsConstructor
public class KafkaEventsConsumer {
    private static final String EVENT_TYPE_JSON_KEY = "event_type";
    private final IntegrationEventFanoutDelivererService integrationEventFanoutDelivererService;
    private final KnownIntegrationEventTypeResolver knownIntegrationEventTypeResolver;
    private final KafkaConsumerProperties kafkaConsumerProperties;
    private final ObjectMapper objectMapper;

    @Transactional
    @KafkaListener(
            topics = "#{kafkaConsumerProperties.topicsForConsume()}",
            groupId = "#{kafkaConsumerProperties.consumerGroupId()}"
    )
    public void consume(@Payload String message, Acknowledgment ack) {
        log.info("Message consumed {}", message);
        message = clearSpecialChars(message);
        Optional<? extends Class<? extends IntegrationEvent>> knownEventClass = resolveEventClassFromMessage(message);
        if (knownEventClass.isPresent()) {
            IntegrationEvent event = extractEvent(message, knownEventClass);
            log.info("Event parsed: {}", event);
            integrationEventFanoutDelivererService.deliverEvent(event);
            log.info("Message delivered");
        } else {
            log.info("Unknown event");
        }
        ack.acknowledge();
    }

    @SneakyThrows
    private IntegrationEvent extractEvent(
            String message,
            Optional<? extends Class<? extends IntegrationEvent>> knownEventClass
    ) {
        return objectMapper.readValue(message, knownEventClass.orElseThrow());
    }

    @SneakyThrows
    private Optional<? extends Class<? extends IntegrationEvent>> resolveEventClassFromMessage(
            String message
    ) {
        JsonNode json = objectMapper.readTree(message);

        String event_type = json.get(EVENT_TYPE_JSON_KEY).textValue();
        if (event_type == null) {
            return Optional.empty();
        }
        Class<? extends IntegrationEvent> integrationEventClass =
                knownIntegrationEventTypeResolver.classByType(event_type);
        return Optional.ofNullable(integrationEventClass);
    }

    private static String clearSpecialChars(String message) {
        return message.replaceAll("^\"|\"$", "").replace("\\\"", "\"");
    }
}
