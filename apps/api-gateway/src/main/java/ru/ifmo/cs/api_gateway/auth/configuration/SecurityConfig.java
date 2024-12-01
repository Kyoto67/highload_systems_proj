package ru.ifmo.cs.api_gateway.auth.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;
import ru.ifmo.cs.jwt_auth.infrastructure.request_filter.JwtTokenAuthenticationFilter;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http,
                                                      JwtTokenAuthenticationFilter authFilter) throws Exception {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .addFilterAt(authFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange((authorize) -> authorize
//                                .pathMatchers("**").permitAll()
                                .pathMatchers("/actuator/**")
                                    .permitAll()
                                .pathMatchers("/api/v*/auth/authorized-token")
                                    .permitAll()
                                .pathMatchers("/api/v*/auth/**")
                                    .hasAuthority("supervisor")
                                .pathMatchers("/api/v*/interview-results", "/api/v*/interview-results/**")
                                    .hasAuthority("staff")
                                .pathMatchers("/api/v*/candidates", "/api/v*/candidates/by-id")
                                    .hasAnyAuthority("hr", "interviewer")
                                .pathMatchers("/api/v*/candidates/add")
                                    .hasAuthority("hr")
                                .pathMatchers("/api/v*/feedbacks/pending-result")
                                    .hasAuthority("staff")
                                .pathMatchers("/api/v*/feedbacks", "/api/v*/feedbacks/**")
                                    .hasAuthority("interviewer")
                                .pathMatchers("/api/v*/interviews/cancel")
                                    .hasAnyAuthority("hr", "interviewer")
                                .pathMatchers("/api/v*/interviews", "/api/v*/interviews/**")
                                    .hasAuthority("interviewer")
                                .pathMatchers("/api/v*/interviewers", "/api/v1/interviewers/**")
                                    .hasAnyAuthority("hr", "staff")
                );
        return http.build();
    }

}
