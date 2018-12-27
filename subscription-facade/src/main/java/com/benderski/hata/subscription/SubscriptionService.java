package com.benderski.hata.subscription;

public interface SubscriptionService {
    SubscriptionModel createProfile(Integer userId);
    SubscriptionModel getProfile(Integer userId);
    boolean startSubscription(Integer userId);
    boolean unsubscribe(Integer userId);
}
