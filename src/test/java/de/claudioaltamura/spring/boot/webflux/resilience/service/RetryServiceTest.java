package de.claudioaltamura.spring.boot.webflux.resilience.service;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;

import static io.netty.handler.codec.http.HttpResponseStatus.SERVICE_UNAVAILABLE;

class RetryServiceTest {

    private static MockWebServer mockWebServer;

    private final RetryService retryService = new RetryService(WebClient.create(mockWebServer.url("/").toString()));

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
    void retry() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(SERVICE_UNAVAILABLE.code()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(SERVICE_UNAVAILABLE.code()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(SERVICE_UNAVAILABLE.code()));
        var mockResponse = new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody("{\"name\":\"Spider-Men\"}");
        mockWebServer.enqueue(mockResponse);

        StepVerifier.create(retryService.findSuperheroByCity("NYC")).expectNextMatches(superhero -> "Spider-Men".equals(superhero.name())).expectComplete().verify();
    }
}