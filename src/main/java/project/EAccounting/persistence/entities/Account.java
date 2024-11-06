package project.EAccounting.persistence.entities;

import jakarta.persistence.*;

import project.EAccounting.persistence.entities.datatypes.AccountTypes;

import java.util.List;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private AccountTypes type;

    @Column(name = "avail_balance")
    private int availBalance;

    @Column(name = "credit_card_limit")
    private int creditCardLimit;

    private String description;

    @OneToMany(mappedBy = "account1", cascade = CascadeType.REMOVE)
    private List<Transaction> transactions1;

    @OneToMany(mappedBy = "account2", cascade = CascadeType.REMOVE)
    private List<Transaction> transactions2;


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public AccountTypes getType() { return type; }
    public void setType(AccountTypes type) { this.type = type; }

    public int getAvailBalance() { return availBalance; }
    public void setAvailBalance(int availBalance) { this.availBalance = availBalance; }

    public int getCreditCardLimit() { return creditCardLimit; }
    public void setCreditCardLimit(int creditCardLimit) { this.creditCardLimit = creditCardLimit; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Transaction> getTransactions1() { return transactions1; }
    public List<Transaction> getTransactions2() { return transactions2; }

    public String toString() {
        return String.format("|%-20.20s|%-10d|%-11.11s|%-15d|%-10d|%-50.50s|\n", name,
                    id, type, availBalance, creditCardLimit, description);
    }
}
