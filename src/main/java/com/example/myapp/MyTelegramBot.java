package com.example.myapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class MyTelegramBot extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(MyTelegramBot.class);
    private static String botUsername;
    private static String botToken;

    static {
        botUsername = System.getenv("BOT_USERNAME");
        botToken = System.getenv("BOT_TOKEN");

        if (botUsername == null || botToken == null) {
            logger.error("BOT_USERNAME или BOT_TOKEN не найдены в переменных окружения!");
        } else {
            logger.info("Переменные окружения загружены успешно.");
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Обработка сообщений из канала
        if (update.hasChannelPost()) {
            Message channelPost = update.getChannelPost();
            long chatId = channelPost.getChatId();
            String userMessage = channelPost.getText();

            logger.info("Сообщение из канала: " + userMessage);

            // Отправляем ответное сообщение в канал
            sendMessage(chatId, "Спасибо за ваше сообщение: " + userMessage);
        }

        // Обработка callback-данных, если они есть
        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            logger.info("Callback получен: " + callbackData);
            sendMessage(chatId, "Вы выбрали: " + callbackData);
        }
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Ошибка при отправке сообщения", e);
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new MyTelegramBot());
            logger.info("Бот успешно запущен!");
        } catch (TelegramApiException e) {
            logger.error("Ошибка при запуске бота", e);
        }
    }
}
