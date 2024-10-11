package utils;

import jakarta.persistence.EntityManager;

public interface CommandExecutor {
    void executeCmd(String[] cmd, long chatId, EntityManager em);
}
