package com.test.telegramBot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.telegramBot.client.GeminiApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class GeminiApiService {

  private final GeminiApiClient geminiApiClient;
  private final ObjectMapper objectMapper;

  public Mono<String> getResponse(String prompt) {
    return geminiApiClient.getResponseMono(prompt)
        .flatMap(response -> Mono.fromCallable(() -> {
          JsonNode jsonNode = objectMapper.readTree(response);
          return jsonNode.at("/candidates/0/content/parts/0/text").asText();
        }))
        //парсинг блочная операция - поэтому она будет выполняться в отдельном пуле потоков
        //boundedElastic - как раз для этих целей
        //веб флакс работает только с пулом где ограниченное число потоков которые все время в работе
        .subscribeOn(Schedulers.boundedElastic())
        .onErrorResume(error -> Mono.just(
            "Request processing error. Please try again later." + error.getMessage()));
  }
}
