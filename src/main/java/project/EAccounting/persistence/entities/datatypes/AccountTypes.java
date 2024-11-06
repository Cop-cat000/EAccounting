package project.EAccounting.persistence.entities.datatypes;

public enum AccountTypes {
    DEBIT, CASH, INVESTMENT, CREDIT_CARD, LOAN;

    public static AccountTypes getTypeFromString(String type) throws IncorrectAccountTypeException {
        if(type.equals("DEBIT")) return DEBIT;
        if(type.equals("CASH")) return CASH;
        if(type.equals("INVESTMENT")) return INVESTMENT;
        if(type.equals("CREDIT_CARD")) return CREDIT_CARD;
        if(type.equals("LOAN")) return LOAN;
        throw new IncorrectAccountTypeException();
    }
}