package com.benderski.hata.subscription;

import java.util.function.Consumer;

public interface SubscriptionService {
    SubscriptionModel createOrGetProfile(Integer userId);
    SubscriptionModel updateProfile(Integer userId, SubscriptionModel model);
    SubscriptionModel getProfile(Integer userId);
    boolean startSubscription(Integer userId, Consumer<String> sendMessage);
    boolean unsubscribe(Integer userId);
    void commit();
}