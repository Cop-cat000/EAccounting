package project.EAccounting.managers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommandManager {
    private final UserManager userManager;
    private final AccountManager accountManager;
    private final TransactionManager transactionManager;


    @Autowired
    public CommandManager(UserManager userManager, AccountManager accountManager,
            TransactionManager transactionManager) {
        this.userManager= userManager;
        this.accountManager = accountManager;
        this.transactionManager = transactionManager;
    }
    
    /*public void process(Command command) {
        if(!command.getCmd()[0].equals("/start")) {
            if(!userManager.isAdded(command)) {
                message.send(command.getChatId(), "You have to start first. Type /start to start");
                return;
            }
        }

        CommandExecutor[] executors = { userManager, accountManager, transactionManager };
        for(CommandExecutor executor : executors) {
            if(executor.canExecute(command)) {
                executor.execute(command);
                return;
            }
        }
        message.send(command.getChatId(), "Command not found");
    }
     */
}
