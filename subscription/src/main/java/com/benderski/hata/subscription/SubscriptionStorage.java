package com.benderski.hata.subscription;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SubscriptionStorage implements SubscriptionService {

    Map<Long, Subscription> subscriptions = new ConcurrentHashMap<>();

    public boolean subscribe(Long chatId) {
        if (subscriptions.containsKey(chatId)) {
            return false;
        }
        subscriptions.put(chatId, new ApartmentSubscription(chatId));
        return true;
    }

    public boolean unsubscribe(Long chatId) {
        if (!subscriptions.containsKey(chatId)) {
            return false;
        }
        subscriptions.remove(chatId);
        return true;
    }

    @Override
    public Collection<Subscription> getAll() {
        return subscriptions.values();
    }
}
