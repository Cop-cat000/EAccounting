import java.sql.Statement;
import java.util.Map;
import java.util.HashMap;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static logs.LogerBot.sendException;
import logs.*;
import utils.CommandHandler;

public class CommandRouter extends CommandHandler {
    //Util fields
    private long chatId;
    private String[] cmd;
    private Map<String,CommandHandler> commands = new HashMap<String,CommandHandler>();

    public CommandRouter(Statement stmt, TelegramClient tc) {
        super(tc, stmt);
        //Objects initialization
        users = new Users(stmt, tc);
        accounts = new Accounts(stmt, tc);
        transactions = new Transactions(stmt, tc);

        commands.put("/start", users);
        
        commands.put("/add_account", accounts);
        commands.put("/edit_account", accounts);
        commands.put("/del_account", accounts);
        commands.put("/display_account", accounts);
        commands.put("/add_transaction", transactions);
        commands.put("/display_transaction", transactions);
    }


    //Object fields
    private Users users;
    private Accounts accounts;
    private Transactions transactions;
    

    //Private methods
    private void commandProcessor() {
        CommandHandler ch;
        ch = commands.get(cmd[0]);
        if(ch == null){
            sendMessage(chatId, "Command not found");
            return;
        }
        ch.executeCmd(cmd, chatId);
    }


    //Public methods
    public void executeCmd(String[] cmd, long chatId) {
        this.cmd = cmd;
        this.chatId = chatId;
        commandProcessor();
    }
}
