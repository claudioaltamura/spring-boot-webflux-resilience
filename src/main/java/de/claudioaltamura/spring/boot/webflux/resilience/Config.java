package de.claudioaltamura.spring.boot.webflux.resilience;

import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

  @Bean
  public Duration cacheTTL() {
    return Duration.ofSeconds(30);
  }

  @Bean
  public Duration delay() {
    return Duration.ofSeconds(3);
  }
}
