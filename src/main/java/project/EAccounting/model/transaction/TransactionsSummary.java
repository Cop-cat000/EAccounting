package project.EAccounting.model.transaction;

import project.EAccounting.persistence.entities.datatypes.TransactionTypes;

import java.util.ArrayList;
import java.util.List;

public class TransactionsSummary {
    private final List<TransactionWrapper> transactions;
    private int income;
    private int spending;

    public TransactionsSummary(List<TransactionWrapper> transactionsInput) {
        transactions = new ArrayList<TransactionWrapper>();

        for(TransactionWrapper transaction : transactionsInput) {
            if(transaction.getType().isClientType()) {
                if(transaction.getType() == TransactionTypes.PAYMENT ||
                        (transaction.getType() == TransactionTypes.TRANSFER && transaction.getSum() < 0))
                    spending += transaction.getSum();

                else if(transaction.getType() != TransactionTypes.BETWEEN &&
                        transaction.getType() != TransactionTypes.CASH_WITHDRAWAL)
                    income += transaction.getSum();

            }

            transactions.add(transaction);
        }
    }

    public List<TransactionWrapper> getTransactions() { return transactions; }

    public int getIncome() { return income; }

    public int getSpending() { return spending; }
}
