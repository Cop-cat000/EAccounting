package project.EAccounting.model.account;

import project.EAccounting.persistence.entities.datatypes.AccountTypes;

import java.util.List;

public class AccountCriteria {

    private List<Integer> id;
    private List<AccountTypes> type;

    public List<Integer> getId() { return id; }
    public void setId(List<Integer> id) { this.id = id; }

    public List<AccountTypes> getType() { return type; }
    public void setType(List<AccountTypes> type) { this.type = type; }
}
