package project.EAccounting.persistence.entities.datatypes;

public enum TransactionTypes {
    BALANCE_CHANGE, CREDIT_CARD_LIMIT_CHANGE, PAYMENT, TRANSFER, UP, BETWEEN, CASH_WITHDRAWAL;

    public static TransactionTypes getTypeFromString(String type) throws IncorrectTransactionTypeException {
        if(type.equals("BALANCE_CHANGE")) return BALANCE_CHANGE;
        if(type.equals("CREDIT_CARD_LIMIT_CHANGE")) return CREDIT_CARD_LIMIT_CHANGE;
        if(type.equals("PAYMENT")) return PAYMENT;
        if(type.equals("TRANSFER")) return TRANSFER;
        if(type.equals("UP")) return UP;
        if(type.equals("BETWEEN")) return BETWEEN;
        if(type.equals("CASH_WITHDRAWAL")) return CASH_WITHDRAWAL;
        throw new IncorrectTransactionTypeException();
    }
}
