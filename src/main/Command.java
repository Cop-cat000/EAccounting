package main;

public class Command {

    private final String[] cmd;

    private final long chatId;

    public Command(String cmd, long chatId) {
        this.cmd = cmd.split(" ");
        this.chatId = chatId;
    }

    public String[] getCmd() { return cmd; }

    public long getChatId() { return chatId; }

    @Override
    public int hashCode() {
        return cmd[0].hashCode();
    }
}
