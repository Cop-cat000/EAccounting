package config;

import java.util.Map;

import org.telegram.telegrambots.meta.generics.TelegramClient;

import persistence.CustomPersistenceUnitInfo;

public final class ProjectConfigData {

    static CustomPersistenceUnitInfo customPersistenceUnitInfo;

    static String botToken;

    static Map<String,String> hibernateProperties;


    public static void setCustomPersistenceUnitInfo(CustomPersistenceUnitInfo cpui) {
        customPersistenceUnitInfo = cpui;
    }

    public static void setBotToken(String token) {
        botToken = token;
    }

    public static void setHibernateProperties(Map<String,String> props) {
        hibernateProperties = props;
    }

    private ProjectConfigData() {}
}
