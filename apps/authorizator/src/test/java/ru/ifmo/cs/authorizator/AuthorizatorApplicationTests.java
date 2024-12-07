package ru.ifmo.cs.authorizator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.ifmo.cs.authorizator.configuration.AuhtorizatorTestsConfiguration;

@SpringBootTest
@Import(AuhtorizatorTestsConfiguration.class)
class AuthorizatorApplicationTests {

    @Test
    void contextLoads() {
    }

}
