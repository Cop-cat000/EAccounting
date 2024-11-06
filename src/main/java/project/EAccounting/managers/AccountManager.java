package project.EAccounting.managers;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import project.EAccounting.persistence.entities.datatypes.AccountTypes;
import project.EAccounting.persistence.entities.datatypes.IncorrectAccountTypeException;
import project.EAccounting.persistence.entities.datatypes.TransactionTypes;
import project.EAccounting.persistence.entities.User;
import project.EAccounting.persistence.entities.Account;
import project.EAccounting.persistence.entities.Transaction;

@Component
public class AccountManager {
    private final EntityManagerFactory entityManagerFactory;
    private final List<Integer> commandHash =
        List.of("/add_account".hashCode(), "/edit_account".hashCode(),
                "/del_account".hashCode(), "/display_account".hashCode());

    @Autowired
    public AccountManager(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    /*
    @Override
    public boolean canExecute(Command command) {
        return commandHash.contains(command.hashCode());
    }

    @Override
    public void execute(Command command) {
        String[] cmd = command.getCmd();

        if(cmd[0].equals("/add_account")) addAccount(command);
        else if(cmd[0].equals("/edit_account")) editAccount(command);
        else if(cmd[0].equals("/del_account")) deleteAccount(command);
        else displayAccount(command);
    }
    

    private void addAccount(Command command) {
        if(command.getCmd().length == 1) printHowToAdd(command);
        else add(command);
    }
    private void printHowToAdd(Command command) {
        String text = "This command must be like the following:\n" +
            "/add_account name type sum credit_card_limit description.\n" + 
            "Where name is the name for the account.\n" +
            "Type of account from the following list:\n" +
            "DEBIT, CASH, INVESTMENT, CREDIT_CARD, LOAN.\n" + 
            "Sum is the sum on your account, but if you specified the type as 'LOAN' the SUM must the sum that you owe.\n" +
            "Credit_card_limit, use it to specify credit card limit if 'TYPE' is 'CREDIT_CARD', otherwise keep this field empty.\n" +
            "Description is optional and its max length is 50 characters\n";
        message.send(command.getChatId(), text);
    }
    private void add(Command command) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        String[] cmd = command.getCmd();
        long chatId = command.getChatId();

        try { 
            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            String name;
            User user = entityManager.find(User.class, chatId);
            AccountTypes type;
            int availBalance;
            int creditCardLimit = 0;
            StringBuilder description = new StringBuilder("");

            //Setting vars
            name = cmd[1];
            try {
                type = AccountTypes.getTypeFromString(cmd[2]);
            } catch(IncorrectAccountTypeException e) {
                message.send(chatId, "Incorrect type");
                e.printStackTrace();
                return;
            }
            try {
                availBalance = Integer.parseInt(cmd[3]);
            } catch(Exception e) {
                message.send(chatId, "Incorrect sum");
                e.printStackTrace();
                return;
            }
            int i = 4;
            if(type == AccountTypes.CREDIT_CARD) {
                i++;
                try {
                    creditCardLimit = Integer.parseInt(cmd[4]);
                } catch(Exception e) {
                    message.send(chatId, "Incorrect credit_card_limit");
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

            entityManager.persist(newAcc);

            entityTransaction.commit();
            message.send(chatId, "Done successfully!");
        } catch(Exception e) {
            message.send(chatId, "Something went wrong");
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }


    private void editAccount(Command command) {
        if(command.getCmd().length == 1) printHowToEdit(command);
        else edit(command);
    }
    private void printHowToEdit(Command command) {
        String text = "This command must be like the following:\n" +
            "/edit_account account_id field new_val.\n" + 
            "Where field is the field of the account you want to change.\n" +
            "Type here -n to change account name, \n" +
            "-b to change balance, -l to change credit limit(for credit cards).\n" +
            "-d to change description\n" +
            "In the field 'new_val' specify new value for the field you have chosen.\n";
        message.send(command.getChatId(), text);
    }
    private void edit(Command command) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        String[] cmd = command.getCmd();
        long chatId = command.getChatId();

        int id;
        String field;
        Account account;
        try { 
            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            
            //Setting vars
            try {
                id = Integer.parseInt(cmd[1]);
                account = entityManager.find(Account.class, id);
                if(account == null || account.getUser().getId() != chatId) 
                    throw new Exception();
            } catch(Exception e) {
                message.send(chatId, "Incorrect account_id");
                e.printStackTrace();
                return;
            }
        
            try {
                field = cmd[2];
                if(!( field.equals("-n") || field.equals("-b") || field.equals("-d") || 
                            (account.getType() == AccountTypes.CREDIT_CARD && field.equals("-l")) ))
                    throw new Exception();
            } catch(Exception e) {
                message.send(chatId, "Incorrect field");
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
                    message.send(chatId, "Incorrect new_val");
                    e.printStackTrace();
                    return;
                }
                int oldBal = account.getAvailBalance();

                account.setAvailBalance(newVal);

                Transaction transaction = new Transaction();
                transaction.setUser(account.getUser());
                transaction.setSum(newVal - oldBal);
                transaction.setDate(LocalDateTime.now());
                transaction.setType(TransactionTypes.BALANCE_CHANGE);
                transaction.setAccount1(account);
                transaction.setComment("OLD BALANCE: " + oldBal + ", NEW BALANCE: " + newVal);

                entityManager.persist(transaction);
            } else {
                int newVal; 
                try {
                    newVal = Integer.parseInt(cmd[3]);
                } catch(Exception e) {
                    message.send(chatId, "Incorrect new_val");
                    e.printStackTrace();
                    return;
                }
                int oldLim = account.getCreditCardLimit();

                account.setCreditCardLimit(newVal);

                Transaction transaction = new Transaction();
                transaction.setUser(account.getUser());
                transaction.setSum(newVal - oldLim);
                transaction.setDate(LocalDateTime.now());
                transaction.setType(TransactionTypes.CREDIT_CARD_LIMIT_CHANGE);
                transaction.setAccount1(account);
                transaction.setComment("OLD LIMIT: " + oldLim + ", NEW LIMIT: " + newVal);

                entityManager.persist(transaction);
            }

            entityTransaction.commit();
            message.send(chatId, "Done successfully!");
        } catch(Exception e) {
            message.send(chatId, "Something went wrong");
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }


    private void deleteAccount(Command command) {
        if(command.getCmd().length == 1) printHowToDelete(command);
        else delete(command);
    }
    private void printHowToDelete(Command command) {
        String text = "This command must be like the following:\n" +
            "/del_account account_id.\n";
        message.send(command.getChatId(), text);
    }
    private void delete(Command command) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        String[] cmd = command.getCmd();
        long chatId = command.getChatId();

        Account account;
        int id;
        try { 
            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            try {
                id = Integer.parseInt(cmd[1]);
                account = entityManager.find(Account.class, id);
                if(account == null || account.getUser().getId() != chatId) 
                        throw new Exception();
            } catch(Exception e) {
                message.send(chatId, "Incorrect account_id");
                e.printStackTrace();
                return;
            }
            entityManager.remove(account);

            entityTransaction.commit();
            message.send(chatId, "Done successfully!");
        } catch(Exception e) {
            message.send(chatId, "Something went wrong");
            e.printStackTrace();
        } finally {
            entityManager.close();
        }
    }


    private void displayAccount(Command command) {
        if(command.getCmd().length == 1) printHowToDisplay(command);
        else display(command);
    }
    private void printHowToDisplay(Command command) {
        String text = "This command must be like the following:\n" +
            "/display_account args.\n" +
            "Instead 'args' you can type \"*\" to list all accounts.\n" +
            "Or you can type specific account id, e.g. \"1 2 3...\".\n" +
            "Or you can type -d to list debit accounts, -cc for credit cards, -l for loans, -i for investments and -c for cash\n" +
                "e.g \"-d -l -i...\".";
        message.send(command.getChatId(), text);
    }
    private void display(Command command) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        String[] cmd = command.getCmd();
        long chatId = command.getChatId();

        String[] args;
        args = Arrays.copyOfRange(cmd, 1, cmd.length);

        int sum = 0;
        int owe = 0;

        User user = entityManager.find(User.class, chatId);
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
            message.send(chatId, doc);
        } catch(Exception e) {
            message.send(chatId, "Something went wrong");
            e.printStackTrace();
        }
    }

     */
}
