package com.test.telegramBot.service;

import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Ben4inBot extends TelegramLongPollingBot {

    private final String botUsername;
    private final GeminiApiService geminiApiService;

    public Ben4inBot(@Value("${telegram.bot.token}") String botToken,
                     @Value("${telegram.bot.username}") String botUsername,
                     GeminiApiService geminiApiService) {
        super(botToken);
        this.botUsername = botUsername;
        this.geminiApiService = geminiApiService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText().trim();
            long chatId = update.getMessage().getChatId();

            System.out.println("Chat ID: " + chatId);

            if (!messageText.toLowerCase().startsWith("bot ")) {
                return;
            }

            String userRequest = messageText.substring(4).trim();
            geminiApiService.getResponse(userRequest)
                .subscribe(
                    response -> sendMessage(chatId, response),
                    error -> {
                        System.err.println("Error processing request: " + error.getMessage());
                        sendMessage(chatId, "Sorry, something went wrong.");
                    }
                );
        }
    }

    public void sendMessage(long chatId, String messageText) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setParseMode("MarkdownV2");
        message.setText(escapeMarkdown(messageText));

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String escapeMarkdown(String text) {
        return text.replaceAll("([*_\\[\\]()~`>#+\\-=|{}.!])", "\\\\$1");
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }
}

