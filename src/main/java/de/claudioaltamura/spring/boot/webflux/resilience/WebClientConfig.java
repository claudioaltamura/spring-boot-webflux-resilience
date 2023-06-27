package de.claudioaltamura.spring.boot.webflux.resilience;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration

public class WebClientConfig {
    @Value("${serviceUrl}")
    private String serviceUrl;

    @Bean
    WebClient webClient(WebClient.Builder builder) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1_000) // millis
                .doOnConnected(connection ->
                        connection
                                .addHandlerLast(new ReadTimeoutHandler(1))); // seconds

        return builder
                .baseUrl(serviceUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

}
