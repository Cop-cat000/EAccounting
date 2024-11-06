package project.EAccounting.model.account;

public class AccountEditedFields {

    private String name;
    private int availBalance = -1;
    private int creditCardLimit = -1;
    private String description;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAvailBalance() { return availBalance; }
    public void setAvailBalance(int availBalance) { this.availBalance = availBalance; }

    public int getCreditCardLimit() { return creditCardLimit; }
    public void setCreditCardLimit(int creditCardLimit) { this.creditCardLimit = creditCardLimit; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
