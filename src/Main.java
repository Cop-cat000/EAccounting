import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import static logs.LogerBot.sendException;
import logs.*;

class Main {
    public static void main(String[] args) {
        DBConnector.connect(args[0], args[1], args[2]);
        String botToken = args[3];
        String logBotToken = args[4];

        try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
            Bot bot = new Bot(botToken);
            botsApplication.registerBot(botToken, bot);
            System.out.println("Bot successfully started!");

            TelegramBotsLongPollingApplication botsApplicationLoger = new TelegramBotsLongPollingApplication();
            LogerBot logerBot = new LogerBot(logBotToken, DBConnector.getStatement());
            botsApplicationLoger.registerBot(logBotToken, logerBot);
            System.out.println("Bot successfully started!");

            Thread.currentThread().join();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
