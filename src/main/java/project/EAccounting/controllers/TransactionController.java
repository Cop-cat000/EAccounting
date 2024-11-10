package project.EAccounting.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import project.EAccounting.annotations.CheckIfLoggedIn;
import project.EAccounting.model.transaction.TransactionCriteria;
import project.EAccounting.model.transaction.TransactionInput;
import project.EAccounting.model.transaction.TransactionWrapper;
import project.EAccounting.model.transaction.TransactionsSummary;
import project.EAccounting.persistence.entities.Transaction;
import project.EAccounting.repositories.AccountRepository;
import project.EAccounting.repositories.TransactionRepository;
import project.EAccounting.services.TransactionCriteriaService;
import project.EAccounting.services.TransactionService;

import java.util.List;

@Controller
public class TransactionController {

    private final AccountRepository accountRepository;
    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;
    private final TransactionCriteriaService criteriaService;

    @Autowired
    public TransactionController(AccountRepository accountRepository, TransactionRepository transactionRepository,
                                 TransactionCriteriaService criteriaService, TransactionService transactionService) {
        this.transactionService = transactionService;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.criteriaService = criteriaService;
    }

    @QueryMapping
    @ResponseBody
    @CheckIfLoggedIn
    TransactionsSummary getTransactions(@Argument(name = "criteria")TransactionCriteria criteria) {
        List<TransactionWrapper> transactions = transactionRepository.retrieveByCriteria(criteria);
        return new TransactionsSummary(transactions);
    }

    @MutationMapping
    @ResponseBody
    @CheckIfLoggedIn
    TransactionsSummary addTransaction(@Argument(name = "newTransaction")TransactionInput input) {
        transactionRepository.store(input);
        return getTransactions(criteriaService.getCriteria());
    }

    @MutationMapping
    @ResponseBody
    @CheckIfLoggedIn
    TransactionsSummary deleteTransaction(@Argument(name = "id") int id) {
        transactionRepository.delete(id);
        return getTransactions(criteriaService.getCriteria());
    }
}
