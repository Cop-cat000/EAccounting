package project.EAccounting.model.transaction;

import java.time.LocalDate;

public class TransactionCriteria {

    private LocalDate startDate;
    private LocalDate endDate;
    private int accountId = -1;

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public int getAccountId() { return accountId; }
    public void setAccountId(int accountId) { this.accountId = accountId; }
}
