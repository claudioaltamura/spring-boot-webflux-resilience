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

class DelayedServiceTest {

  private static MockWebServer mockWebServer;

  private final Duration delay = Duration.ofSeconds(30);

  private final DelayedService delayedService =
      new DelayedService(WebClient.create(mockWebServer.url("/").toString()), delay);

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
  void delay() {
    var mockResponse1 =
        new MockResponse()
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .setBody("{\"name\":\"Spider-Men\"}");
    mockWebServer.enqueue(mockResponse1);

    long start = System.currentTimeMillis();
    StepVerifier.create(delayedService.findSuperheroByCity("NYC"))
        .expectNextMatches(superhero -> "Spider-Men".equals(superhero.name()))
        .verifyComplete();
    long end = System.currentTimeMillis();

    assertThat(end - start).isGreaterThanOrEqualTo(delay.toMillis());

    assertThat(mockWebServer.getRequestCount()).isEqualTo(1);
  }
}
