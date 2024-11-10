package project.EAccounting.exceptions;

public class IncorrectTransactionException extends RuntimeException {

    public IncorrectTransactionException() {
        super("Incorrect transaction");
    }
}
