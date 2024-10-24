package utils;

import org.springframework.stereotype.Component;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;

@Component
public class Message {

    private TelegramClient telegramClient;


    public void send(long chatId, String messageText) {
        SendMessage message = SendMessage // Create a message object
                .builder()
                .chatId(chatId)
                .text(messageText)
                .build();
        try {
            telegramClient.execute(message); // Sending our message object to user
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void send(long chatId, InputFile document) {
        SendDocument sendDoc = SendDocument
                .builder()
                .chatId(chatId)
                .document(document)
                .build();
        try {
            telegramClient.execute(sendDoc);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void setTelegramClient(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }
}
