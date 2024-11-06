package project.EAccounting.model.account;

import project.EAccounting.persistence.entities.Account;

import java.util.List;

public class AccountsSummary {

    private List<Account> accounts;
    private int sum;
    private int debt;
    private int total;

    public List<Account> getAccounts() { return accounts; }
    public void setAccounts(List<Account> accounts) { this.accounts = accounts; }

    public int getSum() { return sum; }
    public void setSum(int sum) { this.sum = sum; }

    public int getDebt() { return debt; }
    public void setDebt(int debt) { this.debt = debt; }

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
}
