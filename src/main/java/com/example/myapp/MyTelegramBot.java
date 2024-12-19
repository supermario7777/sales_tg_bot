package com.example.myapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
        if (update.hasMessage()) {
            Message message = update.getMessage();

            // Проверка: сообщение из группы или канала
            if (message.getChat().isGroupChat() || message.getChat().isSuperGroupChat()) {
                long chatId = message.getChatId();
                String userMessage = message.getText();

                logger.info("Сообщение из группы/канала: " + userMessage);

                // Ответ на сообщение
                sendMessage(chatId, "Спасибо за ваше сообщение! Мы обязательно ответим.");
            }

            // Проверка: сообщение из личного чата
            else if (message.getChat().isUserChat()) {
                long chatId = message.getChatId();
                sendResponseWithButtons(chatId);
            }
        }

        // Обработка нажатий кнопок
        if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
        }
    }

    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        String callbackData = callbackQuery.getData();
        long chatId = callbackQuery.getMessage().getChatId();

        switch (callbackData) {
            case "about_us":
                sendAboutUs(chatId);
                break;
            case "catalogs":
                sendCatalogs(chatId);
                break;
            case "contacts":
                sendContacts(chatId);
                break;
            case "catalog_dentistry":
                sendSubcategories(chatId, "catalog_dentistry");
                break;
            case "catalog_electronics":
                sendSubcategories(chatId, "catalog_electronics");
                break;
            case "subcategory_tools":
                sendPdfFile(chatId, "files/dentistry_tools.pdf");
                break;
            case "subcategory_meds":
                sendPdfFile(chatId, "files/dentistry_meds.pdf");
                break;
            case "subcategory_tv":
                sendPdfFile(chatId, "files/electronics_tv.pdf");
                break;
            case "subcategory_audio":
                sendPdfFile(chatId, "files/electronics_audio.pdf");
                break;
            default:
                logger.warn("Необработанное действие: " + callbackData);
                break;
        }
    }

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

    private void sendCatalogs(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Выберите интересующий вас каталог:");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton dentistryButton = new InlineKeyboardButton();
        dentistryButton.setText("\uD83E\uDDB7 Стоматология");
        dentistryButton.setCallbackData("catalog_dentistry");
        row1.add(dentistryButton);

        InlineKeyboardButton electronicsButton = new InlineKeyboardButton();
        electronicsButton.setText("\uD83D\uDCFA Бытовая техника");
        electronicsButton.setCallbackData("catalog_electronics");
        row1.add(electronicsButton);

        buttons.add(row1);
        markup.setKeyboard(buttons);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Ошибка при отправке кнопок каталогов", e);
        }
    }

    private void sendSubcategories(long chatId, String category) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Выберите подкатегорию:");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        if ("catalog_dentistry".equals(category)) {
            List<InlineKeyboardButton> row1 = new ArrayList<>();
            InlineKeyboardButton toolsButton = new InlineKeyboardButton();
            toolsButton.setText("🔧 Инструменты");
            toolsButton.setCallbackData("subcategory_tools");
            row1.add(toolsButton);

            InlineKeyboardButton medsButton = new InlineKeyboardButton();
            medsButton.setText("💊 Медикаменты");
            medsButton.setCallbackData("subcategory_meds");
            row1.add(medsButton);

            buttons.add(row1);
        } else if ("catalog_electronics".equals(category)) {
            List<InlineKeyboardButton> row1 = new ArrayList<>();
            InlineKeyboardButton tvButton = new InlineKeyboardButton();
            tvButton.setText("📺 Телевизоры");
            tvButton.setCallbackData("subcategory_tv");
            row1.add(tvButton);

            InlineKeyboardButton audioButton = new InlineKeyboardButton();
            audioButton.setText("🎵 Аудиотехника");
            audioButton.setCallbackData("subcategory_audio");
            row1.add(audioButton);

            buttons.add(row1);
        }

        markup.setKeyboard(buttons);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Ошибка при отправке подкатегорий", e);
        }
    }

    private void sendPdfFile(long chatId, String filePath) {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
            if (inputStream == null) {
                logger.error("Файл не найден: " + filePath);
                return;
            }
            InputFile inputFile = new InputFile(inputStream, filePath.substring(filePath.lastIndexOf("/") + 1));

            org.telegram.telegrambots.meta.api.methods.send.SendDocument sendDocument = new org.telegram.telegrambots.meta.api.methods.send.SendDocument();
            sendDocument.setChatId(String.valueOf(chatId));
            sendDocument.setDocument(inputFile);

            execute(sendDocument);
        } catch (TelegramApiException e) {
            logger.error("Ошибка при отправке PDF файла", e);
        }
    }

    private void sendAboutUs(long chatId) {
        sendMessage(chatId, "На этом канале вы найдете все необходимые товары и проверенных поставщиков. Не нашли то, что искали? Напишите нам, и мы поможем!");
    }

    private void sendContacts(long chatId) {
        sendMessage(chatId, "Контакты:\nWhatsApp: https://wa.me/message/57ZPJGS2KXYND1\nInstagram: https://www.instagram.com/mai_china_tovar?igshid=cGJjZzF2MGVwMm5p&utm_source=qr");
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
        } catch (TelegramApiException e) {
            logger.error("Ошибка при запуске бота", e);
        }
    }
}
