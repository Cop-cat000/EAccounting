package main;

import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import config.ProjectConfigData;
import config.ProjectConfig;
import persistence.CustomPersistenceUnitInfo;
import utils.Message;

import java.util.HashMap;

class Main {
    public static void main(String[] args) {
        String puName = "EAccounting_persistence_unit";
        String url = args[0];
        String username = args[1];
        String passwd = args[2];
        CustomPersistenceUnitInfo customPersistenceUnitInfo = new CustomPersistenceUnitInfo(puName, url, username, passwd);

        String botToken = args[3];

        ProjectConfigData.setCustomPersistenceUnitInfo(customPersistenceUnitInfo);
        ProjectConfigData.setBotToken(botToken);
        ProjectConfigData.setHibernateProperties(new HashMap<String,String>());

        var context = new AnnotationConfigApplicationContext
            (ProjectConfig.class);

        try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
            Bot bot = context.getBean(Bot.class);

            // Have to set telegram client manually, otherwise Spring falls into circular dependency
            context.getBean(Message.class).setTelegramClient(bot.getTelegramClient());

            botsApplication.registerBot(botToken, bot);
            System.out.println("Bot successfully started!");

            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
