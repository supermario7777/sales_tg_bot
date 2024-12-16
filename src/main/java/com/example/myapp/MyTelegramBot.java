package com.example.myapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
//import io.github.cdimascio.dotenv.Dotenv;


import java.util.ArrayList;
import java.util.List;

public class MyTelegramBot extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(MyTelegramBot.class);
    private static String botUsername;
    private static String botToken;

    static {
        // Загрузка переменных из .env файла
//        Dotenv dotenv = Dotenv.load();
//        botUsername = dotenv.get("BOT_USERNAME");
//        botToken = dotenv.get("BOT_TOKEN");

        botUsername="thisisforexample_bot";
        botToken="7821010099:AAFhSE8jbg_4S56Qldr3mffw_rP-t2ecSIE";

        if (botUsername == null || botToken == null) {
            logger.error("BOT_USERNAME или BOT_TOKEN не найдены в .env файле!");
        } else {
            logger.info("Переменные окружения загружены успешно.");
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            sendResponseWithButtons(message.getChatId());
        }

        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            switch (callbackData) {
                case "about_us":
                    sendAboutUs(chatId);
                    break;
                case "welcome":
                    sendWelcomeText(chatId);
                    break;
                case "catalogs":
                    sendCatalogs(chatId);
                    break;
                case "contacts":
                    sendContacts(chatId);
                    break;
                default:
                    break;
            }
        }
    }

    // Метод для отправки сообщения с кнопками
    private void sendResponseWithButtons(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Здравствуйте! 👋 Добро пожаловать на наш канал! 🌟 Чем мы можем Вам помочь? 🙂");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton aboutUsButton = new InlineKeyboardButton();
        aboutUsButton.setText("\uD83D\uDCD8 О нас");
        aboutUsButton.setCallbackData("about_us");
        row1.add(aboutUsButton);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton catalogsButton = new InlineKeyboardButton();
        catalogsButton.setText("\uD83D\uDCC2 Каталоги");
        catalogsButton.setCallbackData("catalogs");
        row2.add(catalogsButton);

        InlineKeyboardButton contactsButton = new InlineKeyboardButton();
        contactsButton.setText("\uD83D\uDCDE Наши контакты");
        contactsButton.setCallbackData("contacts");
        row2.add(contactsButton);

        buttons.add(row1);
        buttons.add(row2);

        markup.setKeyboard(buttons);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Ошибка при отправке сообщения с кнопками", e);
        }
    }

    // Методы для обработки кнопок
    private void sendAboutUs(long chatId) {
        sendMessage(chatId, "На этом канале вы найдете все необходимые товары и проверенных поставщиков. Не нашли то, что искали? Напишите нам, и мы поможем!");
    }

    private void sendWelcomeText(long chatId) {
        sendMessage(chatId, "Здравствуйте! 👋 Добро пожаловать на наш канал! 🌟 Чем мы можем Вам помочь? 🙂");
    }

    private void sendCatalogs(long chatId) {
        sendMessage(chatId, "📂 Наши каталоги:\n\n" +
                "🦷 Стоматология\n" +
                "📺 Бытовая техника\n" +
                "🚗 Все для авто");
    }

    private void sendContacts(long chatId) {
        sendMessage(chatId, "Контакты:\nWhatsApp: https://wa.me/message/57ZPJGS2KXYND1\nInstagram: https://www.instagram.com/mai_china_tovar?igshid=cGJjZzF2MGVwMm5p&utm_source=qr");
    }

    // Метод для отправки сообщения с кодировкой UTF-8
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
        } catch (TelegramApiException e) {
            logger.error("Ошибка при запуске бота", e);
        }
    }
}
