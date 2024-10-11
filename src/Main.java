import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

import main.Bot;
import persistence.CustomPersistenceUnitInfo;

import java.util.HashMap;

class Main {
    public static void main(String[] args) {
        String puName = "EAccounting_persistence_unit";
        String url = args[0];
        String username = args[1];
        String passwd = args[2];
        CustomPersistenceUnitInfo cpui = new CustomPersistenceUnitInfo(puName, url, username, passwd);

        String botToken = args[3];

        try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
            Bot bot = new Bot(botToken, cpui, new HashMap<String,String>());
            botsApplication.registerBot(botToken, bot);
            System.out.println("Bot successfully started!");

            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
