package project.EAccounting.persistence.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    private String id;

    private int password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<Account> accounts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Transaction> transactions;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public int getPassword() { return password; }
    public void setPassword(int password) { this.password = password; }

    public List<Account> getAccounts() { return accounts; }

    public List<Transaction> getTransactions() { return transactions; }

}
