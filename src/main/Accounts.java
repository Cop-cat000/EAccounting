package main;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.time.LocalDateTime;

import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import utils.MessageSender;
import utils.CommandExecutor;
import utils.file.FileProcessor;
import persistence.entities.User;
import persistence.entities.Account;
import persistence.entities.Transaction;


public class Accounts extends MessageSender implements CommandExecutor {
    private long chatId;
    private String[] cmd;
    private final FileProcessor fileProcessor;
    private EntityManager em;

    public Accounts(TelegramClient tc, FileProcessor fileProcessor) {
        super(tc);
        this.fileProcessor = fileProcessor;
    }

    public void executeCmd(String[] cmd, long chatId, EntityManager em) {
        this.cmd = cmd;
        this.chatId = chatId;
        this.em = em;

        if(cmd[0].equals("/add_account")) addAccount();
        else if(cmd[0].equals("/edit_account")) editAccount();
        else if(cmd[0].equals("/del_account")) deleteAccount();
        else displayAccount();
    }
    

    private void addAccount() {
        if(cmd.length == 1) printHowToAdd();
        else add();
    }
    private void printHowToAdd() {
        String message = "This command must be like the following:\n" +
            "/add_account name type sum credit_card_limit description.\n" + 
            "Where name is the name for the account.\n" +
            "Type of account from the following list:\n" +
            "DEBIT, CASH, INVESTMENT, CREDIT_CARD, LOAN.\n" + 
            "Sum is the sum on your account, but if you specified the type as 'LOAN' the SUM must the sum that you owe.\n" +
            "Credit_card_limit, use it to specify credit card limit if 'TYPE' is 'CREDIT_CARD', otherwise keep this field empty.\n" +
            "Description is optional and its max length is 50 characters\n";
        sendMessage(chatId, message);
    }
    private void add() {
        try { 
            EntityTransaction entityTransaction = em.getTransaction();
            entityTransaction.begin();
        
            String name;
            User user = em.find(User.class, chatId);
            String type;
            int availBalance;
            int creditCardLimit = 0;
            StringBuilder description = new StringBuilder("");
            List<String> types = List.of("DEBIT", "CASH", "INVESTMENT", "CREDIT_CARD", "LOAN");

            //Setting vars
            name = cmd[1];
            try {
                type = cmd[2];
                if(!types.contains(type)) throw new Exception();
            } catch(Exception e) {
                sendMessage(chatId, "Incorrect type");
                e.printStackTrace();
                return;
            }
            try {
                availBalance = Integer.parseInt(cmd[3]);
            } catch(Exception e) {
                sendMessage(chatId, "Incorrect sum");
                e.printStackTrace();
                return;
            }
            int i = 4;
            if(type.equals("CREDIT_CARD")) {
                i++;
                try {
                    creditCardLimit = Integer.parseInt(cmd[4]);
                } catch(Exception e) {
                    sendMessage(chatId, "Incorrect credit_card_limit");
                    e.printStackTrace();
                    return;
                }
            }
            if(cmd.length > i) {
                while(i < cmd.length)
                    description.append(cmd[i++]).append(" ");
            }

            //Persisting
            Account newAcc = new Account();
            newAcc.setName(name);
            newAcc.setUser(user);
            newAcc.setType(type);
            newAcc.setAvailBalance(availBalance);
            newAcc.setCreditCardLimit(creditCardLimit);
            newAcc.setDescription(description.toString());

            em.persist(newAcc);

            entityTransaction.commit();
            sendMessage(chatId, "Done successfully!");
        } catch(Exception e) {
            sendMessage(chatId, "Something went wrong");
            e.printStackTrace();
        } finally {
            em.close();
        }
    }


    private void editAccount() {
        if(cmd.length == 1) printHowToEdit();
        else edit();
    }
    private void printHowToEdit() {
        String message = "This command must be like the following:\n" +
            "/edit_account account_id field new_val.\n" + 
            "Where field is the field of the account you want to change.\n" +
            "Type here -n to change account name, \n" +
            "-b to change balance, -l to change credit limit(for credit cards).\n" +
            "-d to change description\n" +
            "In the field 'new_val' specify new value for the field you have chosen.\n";
        sendMessage(chatId, message);
    }
    private void edit() {
        int id;
        String field;
        Account account;
        try { 
            EntityTransaction entityTransaction = em.getTransaction();
            entityTransaction.begin();
            
            //Setting vars
            try {
                id = Integer.parseInt(cmd[1]);
                account = em.find(Account.class, id);
                if(account == null || account.getUser().getId() != chatId) 
                    throw new Exception();
            } catch(Exception e) {
                sendMessage(chatId, "Incorrect account_id");
                e.printStackTrace();
                return;
            }
        
            try {
                field = cmd[2];
                if(!( field.equals("-n") || field.equals("-b") || field.equals("-d") || 
                            (account.getType().equals("CREDIT_CARD") && field.equals("-l")) ))
                    throw new Exception();
            } catch(Exception e) {
                sendMessage(chatId, "Incorrect field");
                e.printStackTrace();
                return;
            }
    
            //Applying changes
            if(field.equals("-n")) {
                String newVal = cmd[3];
                account.setName(newVal);
            } else if(field.equals("-d")) {
                StringBuilder newVal = new StringBuilder("");
                for(int i = 3; i < cmd.length; i++)
                    newVal.append(cmd[i]).append(" ");
                account.setDescription(newVal.toString());
            } else if(field.equals("-b")) {
                int newVal; 
                try {
                    newVal = Integer.parseInt(cmd[3]);
                } catch(Exception e) {
                    sendMessage(chatId, "Incorrect new_val");
                    e.printStackTrace();
                    return;
                }
                int oldBal = account.getAvailBalance();

                account.setAvailBalance(newVal);

                Transaction transaction = new Transaction();
                transaction.setUser(account.getUser());
                transaction.setSum(newVal - oldBal);
                transaction.setDate(LocalDateTime.now());
                transaction.setType("BALANCE CHANGE");
                transaction.setAccount1(account);
                transaction.setComment("OLD BALANCE: " + oldBal + ", NEW BALANCE: " + newVal);

                em.persist(transaction);
            } else {
                int newVal; 
                try {
                    newVal = Integer.parseInt(cmd[3]);
                } catch(Exception e) {
                    sendMessage(chatId, "Incorrect new_val");
                    e.printStackTrace();
                    return;
                }
                int oldLim = account.getCreditCardLimit();

                account.setCreditCardLimit(newVal);

                Transaction transaction = new Transaction();
                transaction.setUser(account.getUser());
                transaction.setSum(newVal - oldLim);
                transaction.setDate(LocalDateTime.now());
                transaction.setType("CREDIT LIMIT CHANGE");
                transaction.setAccount1(account);
                transaction.setComment("OLD LIMIT: " + oldLim + ", NEW LIMIT: " + newVal);

                em.persist(transaction);
            }

            entityTransaction.commit();
            sendMessage(chatId, "Done successfully!");
        } catch(Exception e) {
            sendMessage(chatId, "Something went wrong");
            e.printStackTrace();
        } finally {
            em.close();
        }
    }


    private void deleteAccount() {
        if(cmd.length == 1) printHowToDelete();
        else delete();
    }
    private void printHowToDelete() {
        String message = "This command must be like the following:\n" +
            "/del_account account_id.\n";
        sendMessage(chatId, message);
    }
    private void delete() {
        Account account;
        int id;
        try { 
            EntityTransaction entityTransaction = em.getTransaction();
            entityTransaction.begin();

            try {
                id = Integer.parseInt(cmd[1]);
                account = em.find(Account.class, id);
                if(account == null || account.getUser().getId() != chatId) 
                        throw new Exception();
            } catch(Exception e) {
                sendMessage(chatId, "Incorrect account_id");
                e.printStackTrace();
                return;
            }
            em.remove(account);

            entityTransaction.commit();
            sendMessage(chatId, "Done successfully!");
        } catch(Exception e) {
            sendMessage(chatId, "Something went wrong");
            e.printStackTrace();
        } finally {
            em.close();
        }
    }


    private void displayAccount() {
        if(cmd.length == 1) printHowToDisplay();
        else display();
    }
    private void printHowToDisplay() {
        String message = "This command must be like the following:\n" +
            "/display_account args.\n" +
            "Instead 'args' you can type \"*\" to list all accounts.\n" +
            "Or you can type specific account id, e.g. \"1 2 3...\".\n" +
            "Or you can type -d to list debit accounts, -cc for credit cards, -l for loans, -i for investments and -c for cash\n" +
                "e.g \"-d -l -i...\".";
        sendMessage(chatId, message);
    }
    private void display() {
        String[] args;
        args = Arrays.copyOfRange(cmd, 1, cmd.length);

        int sum = 0;
        int owe = 0;

        User user = em.find(User.class, chatId);
        List<Account> accounts = user.getAccounts();
        StringBuilder result = new StringBuilder("");
        result.append("---------------------------------------------------" +
                "-----------------------------------------------------------------------\n");
        result.append(String.format("|%-20.20s|%-10.10s|%-11.11s|%-15.15s|%-10.10s|%-50.50s|\n", "name",
                    "id", "type", "balance", "credit_limit", "description"));
        result.append("---------------------------------------------------" +
                "-----------------------------------------------------------------------\n");

        if(args[0].equals("*")) {
            for(Account account : accounts) {
                if(account.getType().equals("LOAN"))
                    owe += account.getAvailBalance();
                else
                    sum += account.getAvailBalance();
                owe += account.getCreditCardLimit();

                result.append(account.toString());
            }
        } else if(args[0].charAt(0) == '-') {
            Map<String,String> flags = new HashMap<String,String>();
            flags.put("DEBIT", "-d");
            flags.put("CREDIT_CARD", "-cc");
            flags.put("LOAN", "-l");
            flags.put("INVESTMENT", "-i");
            flags.put("CASH", "-c");

            List<String> argsList = Arrays.asList(args);
            for(Account account : accounts) {
                if( argsList.contains( flags.get( account.getType() ) ) ) {
                    if(account.getType().equals("LOAN"))
                        owe += account.getAvailBalance();
                    else
                        sum += account.getAvailBalance();
                    owe += account.getCreditCardLimit();

                    result.append(account.toString());
                }
            }
        } else if(Character.isDigit(args[0].charAt(0))) {
            List<Short> argsList = new ArrayList<Short>();
            for(String num : args) {
                short x;
                try {
                    x = Short.parseShort(num);
                } catch(Exception e) {
                    continue;
                }
                argsList.add(x);
            }
            for(Account account : accounts) {
                if( argsList.contains( account.getId() ) ) {
                    if(account.getType().equals("LOAN"))
                        owe += account.getAvailBalance();
                    else
                        sum += account.getAvailBalance();
                    owe += account.getCreditCardLimit();

                    result.append(account.toString());
                }
            }
        }
        result.append("---------------------------------------------------" +
                "-----------------------------------------------------------------------\n");
        result.append("According to this information\n" +
                "You have " + sum + "\n" +
                "You owe " + owe + "\n" +
                "So totally you have " + (sum - owe));
        InputFile doc;
        try {
            doc = new InputFile(fileProcessor.getFile(result.toString(), "Accounts-info.txt"));
            sendMessage(chatId, doc);
        } catch(Exception e) {
            sendMessage(chatId, "Something went wrong");
            e.printStackTrace();
        }
    }
}
