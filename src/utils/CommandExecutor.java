package utils;

import main.Command;

public interface CommandExecutor {
    boolean canExecute(Command command);
    void execute(Command command);
}
