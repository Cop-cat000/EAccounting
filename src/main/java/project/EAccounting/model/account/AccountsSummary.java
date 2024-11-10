package project.EAccounting.model.account;

import project.EAccounting.persistence.entities.Account;
import project.EAccounting.persistence.entities.datatypes.AccountTypes;

import java.util.ArrayList;
import java.util.List;

public class AccountsSummary {

    private final List<Account> accounts;
    private int sum;
    private int debt;
    private int total;

    public AccountsSummary(List<Account> accountsInput) {
        accounts = new ArrayList<Account>();
        for(Account account : accountsInput) {
            if(account.getType() == AccountTypes.LOAN)
                debt += account.getAvailBalance();
            else
                sum += account.getAvailBalance();
            debt += account.getCreditCardLimit();

            accounts.add(account);
        }

        total = sum - debt;
    }

    public List<Account> getAccounts() { return accounts; }

    public int getSum() { return sum; }

    public int getDebt() { return debt; }

    public int getTotal() { return total; }
}
