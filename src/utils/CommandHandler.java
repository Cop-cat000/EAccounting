package utils;

import org.telegram.telegrambots.meta.generics.TelegramClient;

import static logs.LogerBot.sendException;
import java.sql.Statement;
import java.sql.ResultSet;

public abstract class CommandHandler extends MessageSender implements CommandExecutor {
    protected final Statement stmt;

    public CommandHandler(TelegramClient tc, Statement stmt) {
        super(tc);
        this.stmt = stmt;
    }
    
    protected boolean checkAcc(int accId, long chatId) { //Checks if the account belongs 
                                                         //to the user getting this acc and if the acc exists
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
