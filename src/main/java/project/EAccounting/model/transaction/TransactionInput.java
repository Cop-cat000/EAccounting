package project.EAccounting.model.transaction;

import jakarta.persistence.EntityManager;
import project.EAccounting.persistence.entities.Transaction;

import java.time.LocalDateTime;

public class TransactionInput {
    private int sum;
    private LocalDateTime date;
    private ClientTransactionTypes type;
    private int accountId1;
    private int accountId2;
    private String comment;

    public int getSum() { return sum; }
    public void setSum(int sum) { this.sum = sum; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public ClientTransactionTypes getType() { return type; }
    public void setType(ClientTransactionTypes type) { this.type = type; }

    public int getAccountId1() { return accountId1; }
    public void setAccountId1(int accountId1) { this.accountId1 = accountId1; }

    public int getAccountId2() { return accountId2; }
    public void setAccountId2(int accountId2) { this.accountId2 = accountId2; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Transaction makeEntity() {
        Transaction transaction = new Transaction();
        transaction.setSum(sum);
        transaction.setDate(date);
        transaction.setType(type.convert());
        transaction.setComment(comment);

        return transaction;
    }
}
