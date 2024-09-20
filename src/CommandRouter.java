import java.sql.Statement;
import java.util.Map;
import java.util.HashMap;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static logs.LogerBot.sendException;
import logs.*;

public class CommandRouter extends MessageSender {
    //Util fields
    private Statement stmt;
    private long chatId;
    private String[] cmd;
    private Map<String,CommandExecutor> commands = new HashMap<String,CommandExecutor>();

    public CommandRouter(Statement stmt, TelegramClient tc) {
        super(tc);
        //Objects initialization
        this.stmt = stmt;
        users = new Users(stmt, tc);
        accounts = new Accounts(stmt, tc);
        commands.put("/start", users);
        
        commands.put("/add_account", accounts);
        commands.put("/edit_account", accounts);
        commands.put("/del_account", accounts);
        commands.put("/display_account", accounts);
        
    }


    //Object fields
    private Users users;
    private Accounts accounts;
    

    //Private methods
    private void commandProcessor() {
        CommandExecutor ce;
        ce = commands.get(cmd[0]);
        if(ce == null){
            sendMessage(chatId, "Command not found");
            return;
        }
        ce.executeCmd(cmd, chatId);
    }


    //Public methods
    public void getCmd(String cmd, long chatId) {
        this.cmd = cmd.split(" ");
        this.chatId = chatId;
        commandProcessor();
    }
}
