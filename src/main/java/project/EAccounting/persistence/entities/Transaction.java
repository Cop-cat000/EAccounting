package project.EAccounting.persistence.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import project.EAccounting.persistence.entities.datatypes.TransactionTypes;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private int sum;

    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    private TransactionTypes type;

    @ManyToOne
    @JoinColumn(name = "account_id1")
    private Account account1;
    
    @ManyToOne
    @JoinColumn(name = "account_id2")
    private Account account2;

    private String comment;


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public int getSum() { return sum; }
    public void setSum(int sum) { this.sum = sum; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public TransactionTypes getType() { return type; }
    public void setType(TransactionTypes type) { this.type = type; }

    public Account getAccount1() { return account1; }
    public void setAccount1(Account account1) { this.account1 = account1; }

    public Account getAccount2() { return account2; }
    public void setAccount2(Account account2) { this.account2 = account2; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format("|%-5d|%-11d|%-16.16s|%-20.20s|%-7d|%-7d|%-80.80s|\n", 
                    id, sum, date.format(formatter), type, account1.getId(), account2 == null ? 0 : account2.getId(), comment);
    }
}
