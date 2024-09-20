import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static logs.LogerBot.sendException;
import logs.*;

public class Users extends MessageSender implements CommandExecutor {
    private Statement usrStmt;
    long chatId;
    
    public Users(Statement stmt, TelegramClient tc) {
        super(tc);
        usrStmt = stmt;
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
            rs = usrStmt.executeQuery(sql);
            while(rs.next())
                return;
        } catch(Exception e) {
            sendMessage(chatId, "Something went wrong");
            sendException(e);
            return;
        }

        sql = "INSERT INTO users (id) VALUES (" + chatId + ");";
        try {
            usrStmt.executeQuery(sql);
        } catch(Exception e) {
            sendMessage(chatId, "Something went wrong");
            sendException(e);
            return;
        }
    }
}
