package de.claudioaltamura.spring.boot.webflux.resilience.service;

import de.claudioaltamura.spring.boot.webflux.resilience.model.Superhero;
import java.time.Duration;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
public class RetryService {

  private final WebClient webClient;

  public RetryService(WebClient webClient) {
    this.webClient = webClient;
  }

  public Mono<Superhero> findSuperheroByCity(String city) {
    return this.webClient
        .get()
        .uri(uriBuilder -> uriBuilder.path("/superheroes").queryParam("city", city).build())
        .retrieve()
        .bodyToMono(Superhero.class)
        .retryWhen(
            Retry.backoff(2, Duration.ofMillis(25))
                .filter(WebClientResponseException.class::isInstance)
                .maxAttempts(3));
  }
}
