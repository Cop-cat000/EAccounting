import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static logs.LogerBot.sendException;
import logs.*;
import utils.CommandHandler;

public class Users extends CommandHandler {
    //private Statement usrStmt;
    long chatId;
    
    public Users(Statement stmt, TelegramClient tc) {
        super(tc, stmt);
    }
    

    //Public methods
    public void executeCmd(String[] cmd, long chatId) {
        this.chatId = chatId;
        if(cmd[0].equals("/start")) addUser();
    }

    private void addUser() {
        String sql = "SELECT id FROM users WHERE id = " + chatId + ";";
        ResultSet rs;
        try {
            rs = stmt.executeQuery(sql);
            while(rs.next())
                return;
        } catch(Exception e) {
            sendException(e);
            System.out.println(e.getMessage());
            return;
        }

        sql = "INSERT INTO users (id) VALUES (" + chatId + ");";
        try {
            stmt.executeQuery(sql);
        } catch(Exception e) {
            sendException(e);
            System.out.println(e.getMessage());
            return;
        }
    }
}
