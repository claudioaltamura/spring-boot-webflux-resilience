package de.claudioaltamura.spring.boot.webflux.resilience.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.Duration;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

class CacheServiceTest {

  private static MockWebServer mockWebServer;

  private final Duration cacheTTL = Duration.ofSeconds(100);

  private final CacheService cacheService =
      new CacheService(WebClient.create(mockWebServer.url("/").toString()), cacheTTL);

  @BeforeAll
  static void setup() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();
  }

  @AfterAll
  static void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  @Test
  void cached() {
    var mockResponse1 =
        new MockResponse()
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .setBody("{\"name\":\"Spider-Men Cached 1\"}");
    mockWebServer.enqueue(mockResponse1);

    var mockResponse2 =
        new MockResponse()
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .setBody("{\"name\":\"Spider-Men Cached 1\"}");
    mockWebServer.enqueue(mockResponse2);

    StepVerifier.create(cacheService.findSuperheroByCity("NYC"))
        .expectNextMatches(superhero -> "Spider-Men Cached 1".equals(superhero.name()))
        .verifyComplete();
    StepVerifier.create(cacheService.findSuperheroByCity("NYC"))
        .expectNextMatches(superhero -> "Spider-Men Cached 1".equals(superhero.name()))
        .thenAwait();

    assertThat(mockWebServer.getRequestCount()).isEqualTo(1);
  }
}
