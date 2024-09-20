package logs;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LogerBot implements LongPollingSingleThreadUpdateConsumer {
    private static TelegramClient telegramClient;
    private static Statement stmt;
    long chatId;

    public LogerBot(String botToken, Statement stmt) {
        telegramClient = new OkHttpTelegramClient(botToken);
        this.stmt = stmt;
    }

    @Override
    public void consume(Update update) {
        // We check if the update has a message and the message has text
        if (!(update.hasMessage() && update.getMessage().hasText())) return;
        // Set variables
        //String cmd = update.getMessage().getText();
        chatId = update.getMessage().getChatId();
        addDev();
    }

    public static void sendException(Exception e) {
        ResultSet rs;
        String sql = "SELECT * FROM dev;";
        String messageText = e.getMessage();
        SendMessage message;
        long devId;

        try {
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                devId = rs.getInt(1);
                message = SendMessage // Create a message object
                    .builder()
                    .chatId(devId)
                    .text(messageText)
                    .build();
                try {
                    telegramClient.execute(message); 
                } catch (TelegramApiException tae) {
                    System.out.println(tae.getMessage());
                }
            }
        } catch(SQLException sqle) {
            System.out.println(e.getMessage());
            return;
        }
        
    }

    private void addDev() {
        String sql = "SELECT id FROM dev WHERE id = " + chatId + ";";
        ResultSet rs;
        try {
            rs = stmt.executeQuery(sql);
            while(rs.next())
                return;
        } catch(Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        sql = "INSERT INTO dev (id) VALUES (" + chatId + ");";
        try {
            stmt.executeQuery(sql);
        } catch(Exception e) {
            System.out.println(e.getMessage());
            return;
        }
    }
}
