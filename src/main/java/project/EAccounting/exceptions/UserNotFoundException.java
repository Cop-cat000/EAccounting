package project.EAccounting.exceptions;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super("Incorrect username");
    }
}
