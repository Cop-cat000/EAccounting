package project.EAccounting.exceptions;

public class IncorrectAccountException extends RuntimeException {

    public IncorrectAccountException() {
        super("Incorrect account");
    }
}
