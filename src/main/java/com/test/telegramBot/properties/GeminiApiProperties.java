package com.test.telegramBot.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.api.gemini")
public class GeminiApiProperties {
  @Name("base-url") String baseUrl;
  long timeoutSeconds;
}
