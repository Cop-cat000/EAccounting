package project.EAccounting.model.account;

import project.EAccounting.persistence.entities.datatypes.AccountTypes;

public class AccountInput {

    private String name;
    private AccountTypes type;
    private int availBalance;
    private int creditCardLimit = 0;
    private String description;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public AccountTypes getType() { return type; }
    public void setType(AccountTypes type) { this.type = type; }

    public int getAvailBalance() { return availBalance; }
    public void setAvailBalance(int availBalance) { this.availBalance = availBalance; }

    public int getCreditCardLimit() { return creditCardLimit; }
    public void setCreditCardLimit(int creditCardLimit) { this.creditCardLimit = creditCardLimit; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
