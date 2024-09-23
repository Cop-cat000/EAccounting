import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;

import org.telegram.telegrambots.meta.generics.TelegramClient;

import static logs.LogerBot.sendException;
import logs.*;
import utils.CommandHandler;

public class Transactions extends CommandHandler {
    private long chatId;
    private String[] cmd;

    public Transactions(Statement stmt, TelegramClient tc) {
        super(tc, stmt);
    }

    public void executeCmd(String[] cmd, long chatId) {
        this.cmd = cmd;
        this.chatId = chatId;
        if(cmd[0].equals("/add_transaction")) addTransaction();
        else displayTransaction();
    }




    private void addTransaction() {
        if(cmd.length == 1) printHowToAdd();
        else add();
    }
    private void printHowToAdd() {
        String message = "This command must be like the following:\n" +
            "/add_transaction sum date type 'comment' account_id1 account_id2\n" +
            "Where date must be like 'yyyy-mm-dd hh-mm'\n" +
            "Type must be from the following list:\n" +
            "'PAYMENT, TRANSFER, UP, BETWEEN'\n" +
            "(BETWEEN-between your accounts)\n" +
            "If type is 'BETWEEN' so you should specify also account_id2\n" +
            "In this case account_id2 is where you want to transfer money from account_id1\n" +
            "Otherwise leave it empty\n" +
            "If type is 'TRANSFER' type sum > 0 if you're getting money, otherwise type sum < 0\n" +
            "The comment must be like this 'this is the comment for the transaction'" +
            "'UP' means UP BALANCE";
        sendMessage(chatId, message);
    }
    private void add() {
        int sum;
        String date;
        String type;
        StringBuilder comment = new StringBuilder("");
        int accountId1;
        int accountId2;
        ResultSet rs;
        ArrayList<String> types = new ArrayList<String>();
        Collections.addAll(types, "PAYMENT", "TRANSFER", "UP", "BETWEEN");
        //Setting vars
        try {
            sum = Integer.parseInt(cmd[1]);
            date = cmd[2] + " " + cmd[3];
            type = cmd[4];
            int i = 5;
            if(cmd[5].charAt(0) == '\'') {
                while(cmd[i-1].charAt(cmd[i-1].length()-1) != '\'') {
                    comment.append(cmd[i++]).append(" ");
                }
            }
            comment.deleteCharAt(comment.length()-2);
            comment.deleteCharAt(0);
            accountId1 = Integer.parseInt(cmd[i]);
        } catch(Exception e) {
            sendMessage(chatId, "Incorrect input");
            System.out.println(e.getMessage());
            sendException(e);
            return;
        }
        if(!checkAcc(accountId1, chatId)) {
            sendMessage(chatId, "Incorrect account_id1");
            return;
        }
        if(!types.contains(type)) {
            sendMessage(chatId, "Incorrect type");
            return;
        }


        if(type.equals("BETWEEN")) {
            String sql;
            int accId1Sum = - 1000000000;
            int accId2Sum = - 1000000000;
            try {
                accountId2 = Integer.parseInt(cmd[cmd.length-1]);
                if(!checkAcc(accountId2, chatId)) {
                sendMessage(chatId, "Incorrect account_id2");
                return;
                }
            } catch(Exception e) {
                sendMessage(chatId, "Incorrect account_id2");
                System.out.println(e.getMessage());
                sendException(e);
                return;
            }
            //Inserting transaction
            sql = "INSERT INTO transactions (user_id, sum, date, type, account_id1, account_id2, comment) " +
                "VALUES (" + chatId + ", " + sum + ", '" + date + "', '" + type + "', " + accountId1 + ", " +
                accountId2 + ", " + comment + ");";
            try {
                stmt.execute(sql);
            } catch(Exception e) {
                sendMessage(chatId, "Something went wrong");
                sendException(e);
                System.out.println(e.getMessage());
                return;
            }
            //Changing accounts
            try {
                sql = "SELECT avail_balance FROM accounts WHERE account_id = " + accountId1 + ";";
                rs = stmt.executeQuery(sql);
                while(rs.next())
                    accId1Sum = rs.getInt(1);
                sql = "UPDATE accounts SET avail_balance = " + (accId1Sum-sum) + " WHERE account_id = " +
                    accountId1 + ";";
                stmt.execute(sql);
            } catch(Exception e) {
                sendMessage(chatId, "Something went wrong");
                System.out.println(e.getMessage());
                sendException(e);
                return;
            }
            try {
                sql = "SELECT avail_balance FROM accounts WHERE account_id = " + accountId2 + ";";
                rs = stmt.executeQuery(sql);
                while(rs.next())
                    accId2Sum = rs.getInt(1);
                sql = "UPDATE accounts SET avail_balance = " + (accId2Sum+sum) + " WHERE account_id = " +
                    accountId2 + ";";
                stmt.execute(sql);
            } catch(Exception e) {
                sendMessage(chatId, "Something went wrong");
                System.out.println(e.getMessage());
                sendException(e);
                return;
            }
            sendMessage(chatId, "Done successfully");
        } else {
            String sql;
            int accountSum = - 1000000000;

            if(!type.equals("TRANSFER") && sum <= 0) {
                sendMessage(chatId, "Incorrect 'sum'");
                return;
            }
            if(!type.equals("PAYMENT")) sum = -sum;
            //Changing account
            try {
                sql = "SELECT avail_balance FROM accounts WHERE account_id = " + accountId1 + ";";
                rs = stmt.executeQuery(sql);
                while(rs.next())
                    accountSum = rs.getInt(1);
                sql = "UPDATE accounts SET avail_balance = " + (accountSum-sum) + " WHERE account_id = " +
                    accountId1 + ";";
                stmt.execute(sql);
            } catch(Exception e) {
                sendMessage(chatId, "Something went wrong");
                System.out.println(e.getMessage());
                sendException(e);
                return;
            }
            //Inserting transaction
            comment.append(" OLD BALANCE = ").append(Integer.valueOf(accountSum))
                .append(", NEW BALANCE = ").append(Integer.valueOf((accountSum-sum)));
            sql = "INSERT INTO transactions (user_id, sum, date, type, account_id1, comment) " +
                "VALUES (" + chatId + ", " + sum + ", '" + date + "', '" + type + "', " + accountId1 
                + ", '" + comment + "');";
            try {
                stmt.execute(sql);
            } catch(Exception e) {
                sendMessage(chatId, "Something went wrong");
                sendException(e);
                System.out.println(e.getMessage());
                return;
            }
            sendMessage(chatId, "Done successfully");
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
            "Or start and end e.g. 2000-01-01' or 2000-01-01 2010-01-01" +
            "Instead 'account' you can type 1 specific account id to see transactions for that account in that period\n" +
            "Or you can leave it empy to see all the transactions in that period";
        sendMessage(chatId, message);
    }
    private void display() {
        String sql;
        ResultSet rs;
        String startDate = "";
        String endDate = "";
        int accountId = -1;
        StringBuilder message = new StringBuilder("");
        try {
            startDate = cmd[1];
            if(cmd.length > 2) {
                if(!cmd[2].matches("\\d+")) {
                    endDate = cmd[2];
                    if(cmd.length > 3)
                        accountId = Integer.parseInt(cmd[3]);
                } else 
                    accountId = Integer.parseInt(cmd[2]);
            }
        } catch(Exception e) {
            sendMessage(chatId, "Something went wrong");
            sendException(e);
            System.out.println(e.getMessage());
            return;
        }
        sql = "SELECT sum, date, type, account_id1, account_id2, comment FROM " +
            "transactions WHERE date >= '" + startDate +
            "'";
        if(!endDate.equals(""))
            sql += " AND date <= '" + endDate + "'";
        if(accountId != -1)
            sql += " AND (account_id1 = " + accountId + " OR account_id2 = " + accountId + ")";
        sql += " ORDER BY date;";
        message.append("---------------------------------------------------------------------------------------\n");
        message.append(String.format("|%-10.10s|%-10.10s|%-10.10s|%-10.10s|%-10.10s|%-30.30s|\n", 
                    "sum", "date", "type", "acc_id1", "acc_id2", "comment"));
        int income = 0;
        int spending = 0;
        int sum;
        String date;
        String type;
        int accId1;
        int accId2;
        String comment;
        try {
            rs = stmt.executeQuery(sql);
            while(rs.next()) {
                sum = rs.getInt(1);
                date = rs.getString(2);
                type = rs.getString(3);
                accId1 = rs.getInt(4);
                accId2 = rs.getInt(5);
                comment = rs.getString(6);
                message.append(String.format("|%-10d|%-10.10s|%-10.10s|%-10d|%-10d|%-30.30s|\n",
                            sum, date, type, accId1, accId2, comment));
                if(type.equals("PAYMENT") || (type.equals("TRANSFER") && sum < 0))
                    spending += sum;
                else
                    income += sum;
            }
        } catch(Exception e) {
            e.printStackTrace();
            sendMessage(chatId, "Something went wrong");
            sendException(e);
            System.out.println(e.getMessage());
            return;
        }
        message.append("---------------------------------------------------------------------------------------\n");
        message.append("According to this information\n");
        message.append("Your income is " + Integer.valueOf(income) + "\n");
        message.append("Your spending is " + Integer.valueOf(spending) + "\n");
        sendMessage(chatId, message.toString());
    }
}
