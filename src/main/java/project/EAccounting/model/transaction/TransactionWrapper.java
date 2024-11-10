package project.EAccounting.model.transaction;

import project.EAccounting.persistence.entities.Transaction;
import project.EAccounting.persistence.entities.datatypes.TransactionTypes;

import java.time.LocalDateTime;
import java.util.List;

public class TransactionWrapper {
    private int id;
    private int sum;
    private LocalDateTime date;
    private TransactionTypes type;
    private String account1;
    private String account2;
    private String comment;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSum() { return sum; }
    public void setSum(int sum) { this.sum = sum; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public TransactionTypes getType() { return type; }
    public void setType(TransactionTypes type) { this.type = type; }

    public String getAccount1() { return account1; }
    public void setAccount1(String account1) { this.account1 = account1; }

    public String getAccount2() { return account2; }
    public void setAccount2(String account2) { this.account2 = account2; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
