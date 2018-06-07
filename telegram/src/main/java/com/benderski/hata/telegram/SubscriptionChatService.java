package com.benderski.hata.telegram;


import com.benderski.hata.subscription.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

@Service
public class SubscriptionChatService {

    @Autowired
    SubscriptionService subscriptionService;

    public boolean isSubscription(Message message) {
        return message.hasText() && message.getText().equalsIgnoreCase("subscribe!");
    }

    public boolean isUnsubscribe(Message message) {
        return message.hasText() && message.getText().equalsIgnoreCase("unsubscribe");
    }

    public boolean isStart(Message message) {
        return message.hasText() && message.getText().equalsIgnoreCase("/start");
    }

    public SendMessage answerMenu(Message message) {
        return answer(message).setText("subscribe! or unsubscribe");
    }

    public SendMessage processSubscription(Message message) {
        boolean success = subscriptionService.subscribe(message.getChatId());
        return answer(message).setText(success ? "Subscribed" : "Ya already subs");
    }

    public SendMessage processUnsubscription(Message message) {
        boolean success = subscriptionService.unsubscribe(message.getChatId());
        return answer(message).setText(success ? "Unsubscribed" : "Ya not subs");
    }

    public SendMessage answerDefault(Message message) {
        return answer(message).setText("No command found");
    }

    public SendMessage sendToChat(Long chatId) {
        return new SendMessage().setChatId(chatId);
    }

    private SendMessage answer(Message message) {
        return sendToChat(message.getChatId());
    }



}
