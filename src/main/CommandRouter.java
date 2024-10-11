package main;

import java.util.Map;
import java.util.HashMap;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import utils.MessageSender;
import utils.CommandExecutor;
import utils.file.FileProcessor;
import persistence.CustomPersistenceUnitInfo;
import persistence.entities.User;

public class CommandRouter extends MessageSender {
    //Util fields
    private long chatId;
    private String[] cmd;
    private final Map<String,CommandExecutor> commands = new HashMap<String,CommandExecutor>();


    public CommandRouter(TelegramClient tc, CustomPersistenceUnitInfo cpui, Map<String,String> properties) {
        super(tc);
        //Objects initialization
        fileProcessor = new FileProcessor();
        users = new Users(tc);
        accounts = new Accounts(tc, fileProcessor);
        transactions = new Transactions(tc, fileProcessor);
        emf = new HibernatePersistenceProvider()
                .createContainerEntityManagerFactory(
            cpui, properties);

        commands.put("/start", users);
        
        commands.put("/add_account", accounts);
        commands.put("/edit_account", accounts);
        commands.put("/del_account", accounts);
        commands.put("/display_account", accounts);
        commands.put("/add_transaction", transactions);
        commands.put("/del_transaction", transactions);
        commands.put("/display_transaction", transactions);
    }


    //Object fields
    private FileProcessor fileProcessor;
    private Users users;
    private Accounts accounts;
    private Transactions transactions;
    private EntityManagerFactory emf;
    

    //Private methods
    private void commandProcessor() {
        CommandExecutor executor;
        executor = commands.get(cmd[0]);
        if(executor == null){
            sendMessage(chatId, "Command not found");
            return;
        }

        EntityManager em = emf.createEntityManager();
        if(em.find(User.class, chatId) == null) {
            executor.executeCmd(cmd, chatId, em);
            sendMessage(chatId, "You have to start bot with /start command first");
            return;
        }
        executor.executeCmd(cmd, chatId, em);
        if(em.isOpen()) em.close();
    }


    //Public methods
    public void redirect(String[] cmd, long chatId) {
        this.cmd = cmd;
        this.chatId = chatId;
        commandProcessor();
    }
}
