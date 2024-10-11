package persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;

import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    private long id;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Account> accounts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Transaction> transactions;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public List<Account> getAccounts() { return accounts; }

    public List<Transaction> getTransactions() { return transactions; }
}
