package project.EAccounting.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import project.EAccounting.model.account.AccountCriteria;
import project.EAccounting.model.account.AccountEditedFields;
import project.EAccounting.model.account.AccountInput;
import project.EAccounting.model.account.AccountsSummary;
import project.EAccounting.persistence.entities.Account;
import project.EAccounting.persistence.entities.Transaction;
import project.EAccounting.persistence.entities.datatypes.AccountTypes;
import project.EAccounting.persistence.entities.datatypes.TransactionTypes;
import project.EAccounting.repositories.AccountRepository;
import project.EAccounting.repositories.TransactionRepository;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class AccountController {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public AccountController(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @QueryMapping
    AccountsSummary getAccounts(@Argument(name = "criteria") AccountCriteria criteria) {
        AccountsSummary accountsSummary = new AccountsSummary();
        List<Account> accounts;
        int sum = 0;
        int debt = 0;

        if(criteria == null || (criteria.getId() == null && criteria.getType() == null))
            accounts = accountRepository.retrieveAll();

        else if(criteria.getId() != null)
            accounts = accountRepository.retrieveById(criteria.getId());

        else accounts = accountRepository.retrieveByType(criteria.getType());

        for(Account account : accounts) {
            if(account.getType() == AccountTypes.LOAN)
                debt += account.getAvailBalance();
            else
                sum += account.getAvailBalance();
            debt += account.getCreditCardLimit();
        }

        accountsSummary.setAccounts(accounts);
        accountsSummary.setSum(sum);
        accountsSummary.setDebt(debt);
        accountsSummary.setTotal(sum - debt);

        return accountsSummary;
    }

    @MutationMapping
    boolean addAccount(@Argument(name = "newAccount") AccountInput input) {
        Account account = new Account();
        account.setName(input.getName());
        account.setType(input.getType());
        account.setAvailBalance(input.getAvailBalance());
        account.setCreditCardLimit(input.getCreditCardLimit());
        account.setDescription(input.getDescription());

        accountRepository.store(account);

        return true;
    }

    @MutationMapping
    boolean editAccount(@Argument int id, @Argument(name = "editedAccount")AccountEditedFields accountEditedFields) {
        Account account = accountRepository.retrieveById(List.of(id)).get(0);

        if(accountEditedFields.getName() == null &&
           accountEditedFields.getAvailBalance() == -1 &&
           accountEditedFields.getCreditCardLimit() == -1 &&
           accountEditedFields.getDescription() == null) throw new NullPointerException();

        if(accountEditedFields.getName() != null) {
            account.setName(accountEditedFields.getName());
            accountRepository.merge(account);
            return true;
        }
        if(accountEditedFields.getAvailBalance() != -1) {
            int oldBalance = account.getAvailBalance();
            int newBalance = accountEditedFields.getAvailBalance();

            account.setAvailBalance(newBalance);
            accountRepository.merge(account);

            Transaction transaction = new Transaction();
            transaction.setSum(Math.abs(oldBalance - newBalance));
            transaction.setDate(LocalDateTime.now());
            transaction.setType(TransactionTypes.BALANCE_CHANGE);
            transaction.setAccount1(account);
            transaction.setComment("Old balance: " + oldBalance + ", New balance: " + newBalance);

            transactionRepository.store(transaction);
            return true;
        }

        if(accountEditedFields.getCreditCardLimit() != -1) {
            int oldLimit = account.getCreditCardLimit();
            int newLimit = accountEditedFields.getCreditCardLimit();

            account.setCreditCardLimit(accountEditedFields.getCreditCardLimit());
            accountRepository.merge(account);

            Transaction transaction = new Transaction();
            transaction.setSum(Math.abs(oldLimit - newLimit));
            transaction.setDate(LocalDateTime.now());
            transaction.setAccount1(account);
            transaction.setType(TransactionTypes.CREDIT_CARD_LIMIT_CHANGE);
            transaction.setComment("Old credit limit: " + oldLimit + ", New credit limit: " + newLimit);

            transactionRepository.store(transaction);
            return true;
        }

        account.setDescription(accountEditedFields.getDescription());
        accountRepository.merge(account);
        return true;
    }

    @MutationMapping
    boolean deleteAccount(@Argument int id) {
        accountRepository.deleteById(id);
        return true;
    }
}
