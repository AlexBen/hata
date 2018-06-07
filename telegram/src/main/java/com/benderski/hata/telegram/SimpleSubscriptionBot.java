package com.benderski.hata.telegram;

import com.benderski.hata.subscription.SubscriptionNotificator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

@Service
public class SimpleSubscriptionBot extends TelegramLongPollingBot implements SubscriptionNotificator {

    private static Logger LOGGER = Logger.getLogger(SimpleSubscriptionBot.class.getName());

    @Autowired
    SubscriptionChatService subscriptionChatService;

    String botUserName = "benderfirst_bot";
    String token = "569635102:AAFjmRF2ugKoGCHzZYuK7U6QD3eFAWJO3w8";

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            SendMessage message = processAndAnswer(update.getMessage());
            try {
                execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) throws TelegramApiException {
        LOGGER.config(method.toString());
        return super.execute(method);
    }

    private SendMessage processAndAnswer(Message message) {
        if (subscriptionChatService.isStart(message)) {
            return subscriptionChatService.answerMenu(message);
        }
        if (subscriptionChatService.isSubscription(message)) {
            return subscriptionChatService.processSubscription(message);
        }
        if (subscriptionChatService.isUnsubscribe(message)) {
            return subscriptionChatService.processUnsubscription(message);
        }
        return subscriptionChatService.answerDefault(message);
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        updates.forEach(this::onUpdateReceived);
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void notify(Long chatId, String text) {
        try {
            execute(subscriptionChatService.sendToChat(chatId).setText(text));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void setSubscriptionChatService(SubscriptionChatService subscriptionChatService) {
        this.subscriptionChatService = subscriptionChatService;
    }
}