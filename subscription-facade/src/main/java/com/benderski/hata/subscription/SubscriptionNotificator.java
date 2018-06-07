package com.benderski.hata.subscription;

public interface SubscriptionNotificator {

    void notify(Long chatId, String text);
}
