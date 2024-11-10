package project.EAccounting.exceptions;

public class IncorrectCredentialsException extends RuntimeException {

    public IncorrectCredentialsException() {
        super("Incorrect password");
    }
}
