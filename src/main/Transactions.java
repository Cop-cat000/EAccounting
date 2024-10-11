package main;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import utils.CommandExecutor;
import utils.MessageSender;
import utils.file.FileProcessor;
import persistence.entities.User;
import persistence.entities.Account;
import persistence.entities.Transaction;

//All transanction types: BALANCE CHANGE, CREDIT LIMIT CHANGE, PAYMENT, TRANSFER, UP BALANCE, BETWEEN, CASH WITHDRAWAL

public class Transactions extends MessageSender implements CommandExecutor {
    private long chatId;
    private String[] cmd;
    private final FileProcessor fileProcessor;
    private EntityManager em;

    public Transactions(TelegramClient tc, FileProcessor fileProcessor) {
        super(tc);
        this.fileProcessor = fileProcessor;
    }

    public void executeCmd(String[] cmd, long chatId, EntityManager em) {
        this.cmd = cmd;
        this.chatId = chatId;
        this.em = em;

        if(cmd[0].equals("/add_transaction")) addTransaction();
        else if(cmd[0].equals("/del_transaction")) deleteTransaction();
        else displayTransaction();
    }


    private void addTransaction() {
        if(cmd.length == 1) printHowToAdd();
        else add();
    }
    private void printHowToAdd() {
        String message = "This command must be like the following:\n" +
            "/add_transaction sum date type \"comment\" account_id1 account_id2\n" +
            "Where date must be like 'yyyy-mm-dd hh-mm'\n" +
            "Type must be from the following list:\n" +
            "'PAYMENT, TRANSFER, UP, BETWEEN, WITHDRAWAL'\n" +
            "(BETWEEN-between your accounts, WITHDRAWAL-cash withdrawal)\n" +
            "If type is 'BETWEEN' so you should specify also account_id2\n" +
            "In this case account_id2 is where you want to transfer money from account_id1\n" +
            "Otherwise leave it empty\n" +
            "If type is 'TRANSFER' then sum > 0 if you're getting money, otherwise type sum < 0\n" +
            "The comment must be like this 'this is the comment for the transaction', max length for comment-40\n" +
            "'UP' means UP BALANCE";
        sendMessage(chatId, message);
    }
    private void add() {
        try { 
            EntityTransaction entityTransaction = em.getTransaction();
            entityTransaction.begin();

            User user = em.find(User.class, chatId);
            int sum;
            LocalDateTime date;
            String type;
            StringBuilder comment = new StringBuilder("");
            String commentPrefix = "";
            Account account1;
            List<String> types = List.of("PAYMENT", "TRANSFER", "UP", "BETWEEN", "WITHDRAWAL");

            
            try {
                String stringDate = cmd[2];
                String stringTime = cmd[3];

                String[] yearMonthDay = stringDate.split("-");
                int year = Integer.parseInt(yearMonthDay[0]);
                int month = Integer.parseInt(yearMonthDay[1]);
                int day = Integer.parseInt(yearMonthDay[2]);

                String[] hourMinute = stringTime.split("-");
                int hour = Integer.parseInt(hourMinute[0]);
                int minute = Integer.parseInt(hourMinute[0]);

                date = LocalDateTime.of(year, month, day, hour, minute);
            } catch(Exception e) {
                sendMessage(chatId, "Wrong date");
                e.printStackTrace();
                return;
            }
            try {
                type = cmd[4];
                if(!types.contains(type)) throw new Exception();
            } catch(Exception e) {
                sendMessage(chatId, "Wrong type");
                e.printStackTrace();
                return;
            }
            try {
                sum = Integer.parseInt(cmd[1]);
                if(!type.equals("TRANSFER") && sum <= 0) {
                    sendMessage(chatId, "Wrong sum, with this type of 'type' sum must be > 0");
                    throw new Exception();
                }
            } catch(Exception e) {
                sendMessage(chatId, "Wrong sum");
                e.printStackTrace();
                return;
            }
            int i = 5;
            try {
                if(cmd[5].charAt(0) == '"') {
                    while(cmd[i-1].charAt(cmd[i-1].length()-1) != '"') {
                        comment.append(cmd[i++]).append(" ");
                    }
                }
                comment.deleteCharAt(comment.length()-2);
                comment.deleteCharAt(0);
            } catch(Exception e) {
                sendMessage(chatId, "Wrong comment");
                e.printStackTrace();
                return;
            }
            try {
                account1 = em.find(Account.class, Integer.parseInt(cmd[i++]));
                if(account1 == null || account1.getUser().getId() != chatId) 
                    throw new Exception();
            } catch(Exception e) {
                sendMessage(chatId, "Wrong account_id1");
                e.printStackTrace();
                return;
            }

            Transaction transaction = new Transaction();
            transaction.setUser(user);
            transaction.setSum(sum);
            transaction.setDate(date);
            transaction.setType(type);
            transaction.setAccount1(account1);
            if(type.equals("BETWEEN")) {
                Account account2;
                try {
                    account2 = em.find(Account.class, Integer.parseInt(cmd[i]));
                    if(account2 == null || account2.getUser().getId() != chatId) 
                        throw new Exception();
                } catch(Exception e) {
                    sendMessage(chatId, "Wrong account_id2");
                    e.printStackTrace();
                    return;
                }
                transaction.setAccount2(account2);

                account1.setAvailBalance( account1.getAvailBalance() - sum );
                account2.setAvailBalance( account2.getAvailBalance() + sum );
            }
            else if(transaction.equals("TRANSFER") || transaction.equals("UP")) {
                int oldBal = account1.getAvailBalance();
                account1.setAvailBalance(oldBal + sum);
                commentPrefix += "OLD BALANCE: " + oldBal + ", NEW BALANCE: " + (oldBal + sum) + " ";
            }
            else { // PAYMENT, WITHDRAWAL
                int oldBal = account1.getAvailBalance();
                account1.setAvailBalance(oldBal - sum);
                commentPrefix += "OLD BALANCE: " + oldBal + ", NEW BALANCE: " + (oldBal - sum) + " ";
            }
            transaction.setComment(commentPrefix + comment.toString());

            em.persist(transaction);

            entityTransaction.commit();
            sendMessage(chatId, "Done successfully!");
        } catch(Exception e) {
            sendMessage(chatId, "Something went wrong");
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    private void deleteTransaction() {
        if(cmd.length == 1) printHowToDelete();
        else delete();
    }
    private void printHowToDelete() {
        String message = "This command must be like the following:\n" +
            "/del_transaction transaction_id.\n";
        sendMessage(chatId, message);
    }
    private void delete() {
        try { 
            EntityTransaction entityTransaction = em.getTransaction();
            entityTransaction.begin();
            
            Transaction transaction;
            short id;
            try {
                id = Short.parseShort(cmd[1]);
                transaction = em.find(Transaction.class, id);
                if(transaction == null || transaction.getUser().getId() != chatId)
                    throw new Exception();
            } catch(Exception e) {
                sendMessage(chatId, "Wrong transaction_id");
                e.printStackTrace();
                return;
            }
            
            Account account1 = transaction.getAccount1();
            int sum = transaction.getSum();

            if(transaction.getType().equals("BETWEEN")) {
                Account account2 = transaction.getAccount2();

                account1.setAvailBalance( account1.getAvailBalance() + sum );
                account2.setAvailBalance( account2.getAvailBalance() - sum );
            } 
            else if(transaction.getType().equals("PAYMENT") || transaction.getType().equals("WITHDRAWAL")) {
                account1.setAvailBalance( account1.getAvailBalance() + sum );
            } else { //BALANCE CHANGE, CREDIT LIMIT CHANGE, TRANSFER, UP
                account1.setAvailBalance( account1.getAvailBalance() - sum );
            }

            em.remove(transaction);

            entityTransaction.commit();
            sendMessage(chatId, "Done successfully!");
        } catch(Exception e) {
            sendMessage(chatId, "Something went wrong");
            e.printStackTrace();
        } finally {
            em.close();
        }

    }


    private void displayTransaction() {
        if(cmd.length == 1) printHowToDisplay();
        else display();
    }
    private void printHowToDisplay() {
        String message = "This command must be like following:\n" +
            "/display_transaction period account\n" +
            "Instead period you can type either start date only\n" +
            "Or start and end e.g. 2000-01-01 or 2000-01-01 2010-01-01" +
            "Instead 'account' you can type 1 specific account id to see transactions for that account in that period\n" +
            "Or you can leave it empy to see all the transactions in that period";
        sendMessage(chatId, message);
    }
    private void display() {
        StringBuilder result = new StringBuilder("");
        LocalDate date1;
        LocalDate date2 = LocalDate.now().plusDays(1L);
        LocalDateTime dateTime1;
        LocalDateTime dateTime2 = date2.atStartOfDay();

        int income = 0;
        int spending = 0;

        try {
            date1 = LocalDate.parse(cmd[1]);
            dateTime1 = date1.atStartOfDay();
        } catch(Exception e) {
            sendMessage(chatId, "Wrong period");
            e.printStackTrace();
            return;
        }
        if( cmd.length > 3 || (cmd.length > 2 && !cmd[2].matches("\\d+")) ) {
            try {
                date2 = LocalDate.parse(cmd[2]);
                dateTime2 = date2.atStartOfDay();
            } catch(Exception e) {
                sendMessage(chatId, "Wrong period");
                e.printStackTrace();
                return;
            }   
        }

        result.append("-----------------------------------------------------------------------" +
                "-----------------------------------------------------------------------------------\n");
        result.append(String.format("|%-5.5s|%-11.11s|%-16.16s|%-20.20s|%-7.7s|%-7.7s|%-80.80s|\n", 
                    "id", "sum", "date", "type", "acc_id1", "acc_id2", "comment"));
        result.append("-----------------------------------------------------------------------" +
                "-----------------------------------------------------------------------------------\n");

        User user = em.find(User.class, chatId);
        String jpql = "SELECT t FROM Transaction t WHERE t.user = :user AND t.date >= :date1 AND t.date <= :date2";
        TypedQuery<Transaction> tq;
        List<Transaction> transactions;

        if( cmd.length > 3 || (cmd.length > 2 && cmd[2].matches("\\d+")) ) { //if there's account specification
            int id;
            Account account;
            try {
                if(cmd.length > 3) 
                    id = Integer.parseInt(cmd[3]);
                else
                    id = Integer.parseInt(cmd[2]);

                account = em.find(Account.class, id);
                if(account == null || account.getUser().getId() != chatId) 
                        throw new Exception();
            } catch(Exception e) {
                sendMessage(chatId, "Wrong account_id");
                e.printStackTrace();
                return;
            }


            jpql += " AND (t.account1 = :account OR t.account2 = :account)";
            tq = em.createQuery(jpql, Transaction.class);
            tq.setParameter("user", user);
            tq.setParameter("date1", dateTime1);
            tq.setParameter("date2", dateTime2);
            tq.setParameter("account", account);
        }
        else { //if there's no account specification
            tq = em.createQuery(jpql, Transaction.class);
            tq.setParameter("user", user);
            tq.setParameter("date1", dateTime1);
            tq.setParameter("date2", dateTime2);
        }
        transactions = tq.getResultList();
        for(Transaction transaction : transactions) {
            String type = transaction.getType();
            int sum = transaction.getSum();
            if(type.equals("PAYMENT") || (type.equals("TRANSFER") && sum < 0)) 
                spending += sum;
            else if(type.equals("UP") || (type.equals("TRANSFER") && sum > 0)) 
                income += sum;
            result.append( transaction.toString() );
        }
        result.append("-----------------------------------------------------------------------" +
                "-----------------------------------------------------------------------------------\n");
        result.append("According to this information\n");
        result.append("Your income is " + Integer.valueOf(income) + "\n");
        result.append("Your spending is " + Integer.valueOf(spending) + "\n");
        InputFile doc;
        try {
            doc = new InputFile(fileProcessor.getFile(result.toString(), "Transactions-info.txt"));
            sendMessage(chatId, doc);
        } catch(Exception e) {
            sendMessage(chatId, "Something went wrong");
            e.printStackTrace();
        }
    }
}
