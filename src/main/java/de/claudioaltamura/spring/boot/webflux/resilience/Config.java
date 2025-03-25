package de.claudioaltamura.spring.boot.webflux.resilience;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Config {

    @Bean
    public Duration cacheTTL() {
        return Duration.ofSeconds(30);
    }

}
