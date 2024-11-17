package ru.ifmo.cs.jwt_auth.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.ifmo.cs.jwt_auth.infrastructure.jwt.JwtImpl;

@Configuration
public class JwtAuthServiceConfiguration {
    @Bean
    public JwtImpl jwtImpl(
            @Value("${security.jwt.secret-word}") String secret
    ) {
        return new JwtImpl(secret);
    }
}
