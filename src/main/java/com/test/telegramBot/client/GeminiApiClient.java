package com.test.telegramBot.client;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Component
public class GeminiApiClient {

  private final String apiKey;
  private final WebClient webClient;
  private final Retry retryStrategy = Retry.backoff(3, Duration.ofSeconds(2))
      .filter(throwable -> throwable instanceof WebClientResponseException &&
          ((WebClientResponseException) throwable).getStatusCode().value() == 429);

  public GeminiApiClient(
      @Value("${geminiApiKey}") String apiKey,
      @Qualifier("geminiWebClient") WebClient webClient) {

    this.apiKey = apiKey;
    this.webClient = webClient;
  }

  public Mono<String> getResponseMono(String prompt) {

    Map<String, Object> body = Map.of("contents", List.of(
        Map.of("parts", List.of(Map.of("text", prompt)))
    ));

    return webClient.post()
        .uri(uriBuilder -> uriBuilder.path("/v1beta/models/gemini-1.5-flash:generateContent")
            .queryParam("key", apiKey)
            .build())
        .bodyValue(body)
        .retrieve()
        .bodyToMono(String.class)
        .retryWhen(retryStrategy);
  }

}