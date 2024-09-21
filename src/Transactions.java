import java.sql.Statement;
import java.sql.SQLException;
import java.sql.ResultSet;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class Transactions extends MessageSender implements CommandExecutor {
    private Statement stmt;
    private long chatId;
    private String[] cmd;

    public Transactions(Statement stmt, TelegramClient tc) {
        super(tc);
        this.stmt = stmt;
    }

    public void executeCmd(String[] cmd, long chatId) {
        this.cmd = cmd;
        this.chatId = chatId;
        if(cmd[0].equals("/add_transaction")) addTransaction();
        else if(cmd[0].equals("/edit_transaction")) editTransaction();
        else if(cmd[0].equals("/del_transaction")) deleteTransaction();
        else displayTransaction();
    }




    private void addTransaction() {
        if(cmd.length == 1) printHowToAdd();
        else add();
    }
    private void printHowToAdd() {
        String message = "This command must be like the following:\n" +
            "/add_transaction sum date type comment account_id1 account_id2\n" +
            "Where date must be like 'yyyy-mm-dd hh-mm'\n" +
            "Type must be from the following list:\n" +
            "'PAYMENT, TRANSFER, UP BALANCE, BETWEEN'\n" +
            "(BETWEEN-between your accounts)\n" +
            "If type is 'BETWEEN' so you should specify also account_id2\n" +
            "Otherwise leave it empty";
        sendMessage(chatId, message);
    }
    private void add() {
        int sum;
        String date;
        String type;

    }


    private void editTransaction() {
        if(cmd.length == 1) printHowToEdit();
        else edit();
    }
    private void printHowToEdit() {}
    private void edit() {}


    private void deleteTransaction() {
        if(cmd.length == 1) printHowToDelete();
        else delete();
    }
    private void printHowToDelete() {}
    private void delete() {}


    private void displayTransaction() {
        if(cmd.length == 1) printHowToDisplay();
        else display();
    }
    private void printHowToDisplay() {}
    private void display() {}
}
