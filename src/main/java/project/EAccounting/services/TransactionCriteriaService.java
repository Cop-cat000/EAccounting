package project.EAccounting.services;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
import project.EAccounting.model.transaction.TransactionCriteria;

@Service
@SessionScope
public class TransactionCriteriaService {

    private TransactionCriteria criteria;

    public TransactionCriteria getCriteria() { return criteria; }
    public void setCriteria(TransactionCriteria criteria) { this.criteria = criteria; }
}
