import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class Bot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    CommandRouter cr;

    public Bot(String botToken) {
        telegramClient = new OkHttpTelegramClient(botToken);
        cr = new CommandRouter(DBConnector.getStatement(), telegramClient);
    }

    @Override
    public void consume(Update update) {
        // We check if the update has a message and the message has text
        if (!(update.hasMessage() && update.getMessage().hasText())) return;
        // Set variables
        String cmd = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        cr.executeCmd(cmd.split(" "), chatId);
    }
}
