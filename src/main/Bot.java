package main;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import managers.CommandManager;

public class Bot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    private final CommandManager commandManager;

    public Bot(String botToken, CommandManager commandManager) {
        telegramClient = new OkHttpTelegramClient(botToken);
        this.commandManager = commandManager;
    }

    @Override
    public void consume(Update update) {
        // We check if the update has a message and the message has text
        if (!(update.hasMessage() && update.getMessage().hasText())) return;
        // Set variables
        String cmd = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        Command command = new Command(cmd, chatId);
        commandManager.process(command);
    }

    public TelegramClient getTelegramClient() { return telegramClient; }
}
