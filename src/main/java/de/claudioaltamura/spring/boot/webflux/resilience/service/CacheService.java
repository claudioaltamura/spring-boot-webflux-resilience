package de.claudioaltamura.spring.boot.webflux.resilience.service;

import de.claudioaltamura.spring.boot.webflux.resilience.model.Superhero;
import java.time.Duration;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class CacheService {

  private final WebClient webClient;
  private final Duration cacheTTL;

  public CacheService(WebClient webClient, Duration cacheTTL) {
    this.webClient = webClient;
    this.cacheTTL = cacheTTL;
  }

  public Mono<Superhero> findSuperheroByCity(String city) {
    return this.webClient
        .get()
        .uri(uriBuilder -> uriBuilder.path("/superheroes").queryParam("city", city).build())
        .retrieve()
        .bodyToMono(Superhero.class)
        .cache(cacheTTL);
  }
}
