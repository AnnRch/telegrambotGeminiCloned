package com.test.telegramBot.config;

import com.test.telegramBot.service.Ben4inBot;
import com.test.telegramBot.service.GeminiApiService;
import com.test.telegramBot.service.HolidaysParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramConfig {

  @Bean
  public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
    return new TelegramBotsApi(DefaultBotSession.class);
  }

  @Bean
  @DependsOn("telegramBotsApi")
  public Ben4inBot registerBot(@Value("${telegram.bot.token}") String botToken,
      @Value("${telegram.bot.username}") String botUsername,
      TelegramBotsApi botsApi,
      GeminiApiService geminiApiService) throws TelegramApiException {
    Ben4inBot bot = new Ben4inBot(botToken, botUsername, geminiApiService);
    botsApi.registerBot(bot);
    return bot;
  }

  @Bean
  public HolidaysParser holidaysParser(Ben4inBot bot) {
    return new HolidaysParser(bot);
  }
}
