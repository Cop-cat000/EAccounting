package utils;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;

import static logs.LogerBot.sendException;

public class MessageSender {
    private final TelegramClient telegramClient;

    public MessageSender(TelegramClient tc) {
        telegramClient = tc;
    }

    protected void sendMessage(long chatId, String messageText) {
        SendMessage message = SendMessage // Create a message object
                .builder()
                .chatId(chatId)
                .text(messageText)
                .build();
        try {
            telegramClient.execute(message); // Sending our message object to user
        } catch (TelegramApiException e) {
            sendException(e);
            System.out.println(e.getMessage());
        }
    }

    protected void sendMessage(long chatId, InputFile document) {
        SendDocument sendDoc = SendDocument
                .builder()
                .chatId(chatId)
                .document(document)
                .build();
        try {
            telegramClient.execute(sendDoc);
        } catch (TelegramApiException e) {
            sendException(e);
            System.out.println(e.getMessage());
        }
    }
}
