package ru.ifmo.cs.interview_results;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.ifmo.cs.domain_event.application.service.DomainEventFanoutDelivererService;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(initializers = {AbstractIntegrationTest.Initializer.class})
@TestPropertySource(properties = {"spring.config.location=classpath:application-properties.yml"})
public abstract class AbstractIntegrationTest {

    @Autowired
    private DomainEventFanoutDelivererService domainEventFanoutDelivererService;

    private static final String DATABASE_NAME = "spring-app";

    public static PostgreSQLContainer<?> postgreSQLContainer;

    static {
        postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.0")
                .withReuse(true)
                .withDatabaseName(DATABASE_NAME);
        postgreSQLContainer.start();
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "CONTAINER.USERNAME=" + postgreSQLContainer.getUsername(),
                    "CONTAINER.PASSWORD=" + postgreSQLContainer.getPassword(),
                    "CONTAINER.URL=" + postgreSQLContainer.getJdbcUrl(),
                    "CONTAINER.DRIVER-CLASS=" + postgreSQLContainer.getDriverClassName()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @AfterEach
    void tearDown() {
        deliverAllSavedDomainEvents();
    }

    protected void deliverAllSavedDomainEvents() {
        while (domainEventFanoutDelivererService.deliverNext()) ;
    }
}
