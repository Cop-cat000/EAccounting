package main;

import org.telegram.telegrambots.meta.generics.TelegramClient;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import utils.CommandExecutor;
import utils.MessageSender;
import persistence.entities.User;


public class Users extends MessageSender implements CommandExecutor {
    private long chatId;
    private EntityManager em;
    
    public Users(TelegramClient tc) {
        super(tc);
    }
    

    //Public methods
    public void executeCmd(String[] cmd, long chatId, EntityManager em) {
        this.chatId = chatId;
        this.em = em;
        if(cmd[0].equals("/start")) addUser();
    }

    private void addUser() {
        try {
            EntityTransaction transaction = em.getTransaction();
            transaction.begin();

            User user = em.find(User.class, chatId);
            if(user == null) {
                user = new User();
                user.setId(chatId);

                em.persist(user);

                transaction.commit();
                sendMessage(chatId, "Started bot successfully");
            }
            else sendMessage(chatId, "Bot has been already started!");
        
        } catch(Exception e) {
            sendMessage(chatId, "Failed to start bot, try /start again");
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
