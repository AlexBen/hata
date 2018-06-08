package com.benderski.hata.subscription;

public interface SubscriptionService {
    boolean subscribe(Long chatId);
    boolean unsubscribe(Long chatId);
}
