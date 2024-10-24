package managers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.List;

import utils.CommandExecutor;
import utils.Message;
import persistence.entities.User;
import main.Command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class UserManager implements CommandExecutor {
    private final EntityManagerFactory entityManagerFactory;
    private final Message message;
    private final List<Integer> commandHash =
        List.of("/start".hashCode());

    @Autowired
    public UserManager(EntityManagerFactory entityManagerFactory, Message message) {
        this.entityManagerFactory = entityManagerFactory;
        this.message = message;
    }
    
    @Override
    public boolean canExecute(Command command) {
        Integer commandHashCode = (Integer)command.hashCode();
        return commandHash.contains(commandHashCode);
    }

    public boolean isAdded(Command command) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        User user = entityManager.find(User.class, command.getChatId());
        return user == null ? false : true;
    }

    @Override
    public void execute(Command command) {
        addUser(command);
    }


    private void addUser(Command command) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        String[] cmd = command.getCmd();
        long chatId = command.getChatId();
        try {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();

            User user = entityManager.find(User.class, chatId);
            if(user == null) {
                user = new User();
                user.setId(chatId);

                entityManager.persist(user);

                transaction.commit();
                message.send(chatId, "Started bot successfully");
            }
            else message.send(chatId, "Bot has been already started!");
        
        } catch(Exception e) {
            message.send(chatId, "Failed to start bot, try /start again");
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }
}
