package utils;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static logs.LogerBot.sendException;
import logs.*;
import java.sql.Statement;
import java.sql.ResultSet;

public abstract class CommandHandler {
    private final TelegramClient telegramClient;
    protected final Statement stmt;

    public CommandHandler(TelegramClient tc, Statement stmt) {
        telegramClient = tc;
        this.stmt = stmt;
    }

    public abstract void executeCmd(String[] cmd, long chatId);

    protected void sendMessage(long chatId, String messageText) {
        SendMessage message = SendMessage // Create a message object
                .builder()
                .chatId(chatId)
                .text(messageText)
                .build();
        try {
            telegramClient.execute(message); // Sending our message object to user
        } catch (TelegramApiException e) {
            sendException(e);
            System.out.println(e.getMessage());
        }
    }
    protected boolean checkAcc(int accId, long chatId) { //Checks if the account belongs to the user getting this acc and if the acc exists
        String sql = "SELECT user_id FROM accounts WHERE account_id = " + accId + ";";
        try {
            ResultSet rs = stmt.executeQuery(sql);
            int queryId = -1;
            while(rs.next())
                queryId = rs.getInt(1);
            if(queryId == chatId) return true;
            return false;
        } catch (Exception e) {
            sendException(e);
            sendMessage(chatId, "System err");
            System.out.println(e.getMessage());
            return false;
        }
    }
}
