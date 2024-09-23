import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static logs.LogerBot.sendException;
import logs.*;
import utils.CommandHandler;

public class Accounts extends CommandHandler {
    private long chatId;
    private String[] cmd;

    public Accounts(Statement stmt, TelegramClient tc) {
        super(tc, stmt);
    }

    public void executeCmd(String[] cmd, long chatId) {
        this.cmd = cmd;
        this.chatId = chatId;
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
            "/add_account name type sum credit_card_limit.\n" + 
            "Where name is the name for the account.\n" +
            "Type of account from the following list:\n" +
            "DEBIT, CASH, INVESTMENT, CREDIT_CARD, LOAN.\n" + 
            "Sum is the sum on your account, but if you specified the type as 'LOAN' the SUM must the sum that you owe.\n" +
            "Credit_card_limit, use it to specify credit card limit if 'TYPE' is 'CREDIT_CARD', otherwise keep this field empty.";
        sendMessage(chatId, message);
    }
    private void add(){
        ArrayList<String> types = new ArrayList<String>();
        Collections.addAll(types, "DEBIT", "CASH", "INVESTMENT", "CREDIT_CARD", "LOAN");
        String name;
        String type;
        int availBalance;
        try {
            name = cmd[1];
            type = cmd[2];
            availBalance = Integer.parseInt(cmd[3]);
        } catch(Exception e) {
            sendMessage(chatId, "Incorrect input");
            sendException(e);
            System.out.println(e.getMessage());
            return;
        }
        int creditLimit;
        if(!types.contains(type)) {
            sendMessage(chatId, "Incorrect type");
            return;
        }
        String sql = "INSERT INTO accounts (account_name, user_id, type, avail_balance, credit_card_limit) " +
            "VALUES ('" + name + "', " + chatId + ", '" + type + "', " + availBalance + ", ";
        if(type.equals("CREDIT_CARD")) {
            try {
                creditLimit = Integer.parseInt(cmd[4]);
            } catch(Exception e) {
                sendException(e);
                sendMessage(chatId, "Incorrect input(credit_card_limit)");
                System.out.println(e.getMessage());
                return;
            }
            sql += creditLimit + ");";
        }
        else
            sql += "null);";
        try {
            stmt.execute(sql);
            sendMessage(chatId, "Done successfully");
        } catch(SQLException e) {
            sendException(e);
            sendMessage(chatId, "Something went wrong");
            System.out.println(e.getMessage());
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
            "In the field 'new_val' specify new value for the field you have chosen.\n";
        sendMessage(chatId, message);
    }
    private void edit(){
        int accountId;
        String field;
        String sql;

        try {
            accountId = Integer.parseInt(cmd[1]);
            field = cmd[2];
        } catch(Exception e) {
            sendMessage(chatId, "Incorrect input");
            System.out.println(e.getMessage());
            return;
        }
        if(!checkAcc(accountId, chatId)) {
            sendMessage(chatId, "Incorrect account_id");
            return;
        }

        if(field.equals("-n")) {
            String newVal;
            try {
                newVal = cmd[3];
            } catch(Exception e) {
                sendException(e);
                sendMessage(chatId, "Incorrect input (new_val)");
                System.out.println(e.getMessage());
                return;
            }
            sql = "UPDATE accounts SET account_name = '" + newVal + "' WHERE account_id = " + accountId + ";";
            try {
                stmt.execute(sql);
                sendMessage(chatId, "Done successfully");
            } catch(SQLException e) {
                sendException(e);
                sendMessage(chatId, "Something went wrong...");
                System.out.println(e.getMessage());
                return;
            }
        } 
        else if(field.equals("-b")) {
            ResultSet rs;
            int newBalance;
            int oldBalance = -10000000;
            int sum;
            String comment;
            
            //Getting oldBalance
            sql = "SELECT avail_balance FROM accounts WHERE account_id = " + accountId + ";";
            try {
                rs = stmt.executeQuery(sql);
                while(rs.next())
                    oldBalance = rs.getInt(1);
            } catch(SQLException e) {
                sendException(e);
                sendMessage(chatId, "Something went wrong...");
                System.out.println(e.getMessage());
                return;
            }

            //Changing
            try {
                newBalance = Integer.parseInt(cmd[3]);
            } catch(Exception e) {
                sendException(e);
                sendMessage(chatId, "Incorrect input(new_val)");
                System.out.println(e.getMessage());
                return;
            }
            sql = "UPDATE accounts SET avail_balance = " + newBalance + " WHERE account_id = " + accountId + ";";
            try {
                stmt.execute(sql);
                sendMessage(chatId, "Done successfully");
            } catch(SQLException e) {
                sendException(e);
                sendMessage(chatId, "Something went wrong...");
                System.out.println(e.getMessage());
                return;
            }

            //Adding transaction
            sum = Math.abs(oldBalance - newBalance);
            comment = "OLD BALANCE = " + oldBalance + ", NEW BALANCE = " + newBalance + ".";
            sql = "INSERT INTO transactions (user_id, sum, date, type, account_id1, comment) " +
                "VALUES (" + chatId + ", " + sum + ", CURRENT_TIMESTAMP(), 'BALANCE CHANGE', " + accountId + ", '" + comment + "');";
            try {
                stmt.execute(sql);
                sendMessage(chatId, "Done successfully");
            } catch(SQLException e) {
                sendException(e);
                sendMessage(chatId, "Something went wrong...");
                System.out.println(e.getMessage());
                return;
            }
        }
        else if(field.equals("-l")) {
            //Gettin account type
            String type = "0";
            ResultSet rs;
            sql = "SELECT type FROM accounts WHERE account_id = " + accountId + ";";
            try {
                rs = stmt.executeQuery(sql);
                while(rs.next())
                    type = rs.getString(1);
            } catch(SQLException e) {
                sendException(e);
                sendMessage(chatId, "Something went wrong...");
                System.out.println(e.getMessage());
                return;
            }
            if(!type.equals("CREDIT_CARD")) {
                sendMessage(chatId, "Account type must be credit card to do this");
                return;
            }
            
            int newCreditLimit;
            try {
                newCreditLimit = Integer.parseInt(cmd[3]);
            } catch(Exception e) {
                sendException(e);
                sendMessage(chatId, "Incorrect input(new_val)");
                System.out.println(e.getMessage());
                return;
            }
            sql = "UPDATE accounts SET credit_card_limit = " + newCreditLimit + " WHERE account_id = " + accountId + ";";
            try {
                stmt.execute(sql);
                sendMessage(chatId, "Done successfully");
            } catch(SQLException e) {
                sendException(e);
                sendMessage(chatId, "Something went wrong...");
                System.out.println(e.getMessage());
                return;
            }
        }
        else {
            sendMessage(chatId, "Incorrect input");
            return;
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
        int accountId;
        String sql1;
        String sql2;
        
        try {
            accountId = Integer.parseInt(cmd[1]);
        } catch(Exception e) {
            sendException(e);
            sendMessage(chatId, "Incorrect input");
            System.out.println(e.getMessage());
            return;
        }

        if(!checkAcc(accountId, chatId)) {
            sendMessage(chatId, "Incorrect input");
            return;
        }
        sql1 = "DELETE FROM accounts WHERE account_id = " + accountId + ";";
        sql2 = "DELETE FROM transactions WHERE account_id1 = " + accountId + " OR account_id2 = " + accountId + ";";
        try {
            stmt.execute(sql1);
            stmt.execute(sql2);
            sendMessage(chatId, "Done successfully");
        } catch(SQLException e) {
            sendException(e);
            sendMessage(chatId, "Something went wrong...");
            System.out.println(e.getMessage());
            return;
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
    private void display(){
        ResultSet rs;
        String[] args;
        String sql;
        String columns = "account_name, account_id, type, avail_balance, credit_card_limit FROM accounts";

        try {
            args = Arrays.copyOfRange(cmd, 1, cmd.length);
        } catch(Exception e) {
            sendException(e);
            sendMessage(chatId, "Incorrect input");
            System.out.println(e.getMessage());
            return;
        }
        //Making sql
        if(args[0].equals("*"))
            sql = "SELECT " + columns + " WHERE user_id = " + chatId + ";";

        else if(args[0].charAt(0) == '-') {
            sql = "SELECT " + columns + " WHERE user_id = " + chatId + " AND (";
            Map<String,String> flags = new HashMap<String,String>();
            flags.put("-d", "type = 'DEBIT'");
            flags.put("-cc", "type = 'CREDIT_CARD'");
            flags.put("-l", "type = 'LOAN'");
            flags.put("-i", "type = 'INVESTMENT'");
            flags.put("-c", "type = 'CASH'");
            for(int i = 0; i < args.length; i++) {
                sql += flags.get(args[i]);
                if(i < args.length-1) 
                    sql += " OR ";
            } sql += ");";
        } else if(Character.isDigit(args[0].charAt(0))) {
            sql = "SELECT " + columns + " WHERE user_id = " + chatId + " AND (";
            for(int i = 0; i < args.length; i++) {
                if(!args[i].matches("\\d+") || !checkAcc(Integer.valueOf(args[i]), chatId)) {
                    sendMessage(chatId, "Incorrect input");
                    return;
                }
                sql += "account_id = " + Integer.valueOf(args[i]);
                if(i < args.length-1)
                    sql += " OR ";
            } sql += ");";
        } else {
            sendMessage(chatId, "Incorrect input.");
            return;
        }
        //Vars for formatting
        String accName;
        int accId;
        String type;
        int availBal;
        int credit_card_limit;
        
        try { 
            rs = stmt.executeQuery(sql); 
        } catch(SQLException e) {
            sendException(e);
            sendMessage(chatId, "Something went wrong...");
            System.out.println(e.getMessage());
            return;
        }
        int sum = 0;
        int owe = 0;

        //Formatting
        StringBuilder message = new StringBuilder("");
        message.append("--------------------------------------------------------\n");
        message.append(String.format("|%-10.10s|%-10.10s|%-10.10s|%-10.10s|%-10.10s|\n", "name",
                    "id", "type", "balance", "cred lim"));
        message.append("--------------------------------------------------------\n");
        try {
            while(rs.next()) {
                accName = rs.getString(1);
                accId = rs.getInt(2);
                type = rs.getString(3);
                availBal = rs.getInt(4);
                credit_card_limit = rs.getInt(5);
                message.append(String.format("|%-10.10s|%10d|%-10.10s|%10d|%10d|\n", accName, accId,
                            type, availBal, credit_card_limit));
                if(type.equals("LOAN"))
                    owe += availBal;
                else
                    sum += availBal; 
                owe += credit_card_limit;
            }
        } catch(SQLException e) {
            sendException(e);
            sendMessage(chatId, "Something went wrong...");
            System.out.println(e.getMessage());
            return;
        }
        message.append("--------------------------------------------------------\n");
        message.append("According to this information\n" +
                "You have " + sum + "\n" +
                "You owe " + owe + "\n" +
                "So totally you have " + (sum - owe));
        sendMessage(chatId, message.toString());
        sendMessage(chatId, "Copy this message and use monospaced type to see info properly");
    }


    public boolean checkAcc(int accId, long chatId) { //Checks if the account belongs to the user getting this acc and if the acc exists
        
        String sql = "SELECT user_id FROM accounts WHERE account_id = " + accId + ";";
        try {
            ResultSet rs = stmt.executeQuery(sql);
            int queryId = -1;
            while(rs.next())
                queryId = rs.getInt(1);
            if(queryId == chatId) return true;
            return false;
        } catch (SQLException e) {
            sendException(e);
            sendMessage(chatId, "System err");
            System.out.println(e.getMessage());
            return false;
        }
    }
}
