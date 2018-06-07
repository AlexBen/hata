package com.benderski.hata.subscription;

import java.util.Collection;

public interface SubscriptionService {
    boolean subscribe(Long chatId);
    boolean unsubscribe(Long chatId);
    Collection<Subscription> getAll();
}
