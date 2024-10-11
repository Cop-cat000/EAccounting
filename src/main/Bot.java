package main;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;

import persistence.CustomPersistenceUnitInfo;

public class Bot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    CommandRouter router;

    public Bot(String botToken, CustomPersistenceUnitInfo cpui, Map<String,String> properties) {
        telegramClient = new OkHttpTelegramClient(botToken);
        router = new CommandRouter(telegramClient, cpui, properties);
    }

    @Override
    public void consume(Update update) {
        // We check if the update has a message and the message has text
        if (!(update.hasMessage() && update.getMessage().hasText())) return;
        // Set variables
        String cmd = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        router.redirect(cmd.split(" "), chatId);
    }
}
