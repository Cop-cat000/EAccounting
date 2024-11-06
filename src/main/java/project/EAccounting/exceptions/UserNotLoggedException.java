package project.EAccounting.exceptions;

public class UserNotLoggedException extends RuntimeException {
    public UserNotLoggedException() {
        super("You have to log in to continue!");
    }
}
