package de.claudioaltamura.spring.boot.webflux.resilience.service;

import de.claudioaltamura.spring.boot.webflux.resilience.model.Superhero;
import java.time.Duration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class DelayedService {

  private final WebClient webClient;
  private final Duration delay;

  public DelayedService(WebClient webClient, Duration delay) {
    this.webClient = webClient;
    this.delay = delay;
  }

  public Mono<Superhero> findSuperheroByCity(String city) {
    return this.webClient
        .get()
        .uri(uriBuilder -> uriBuilder.path("/superheroes").queryParam("city", city).build())
        .retrieve()
        .bodyToMono(Superhero.class)
        .delayElement(delay);
  }
}
