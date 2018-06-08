package com.benderski.hata.subscription;

import com.benderski.hata.infrastructure.Apartment;
import io.reactivex.Observable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service
public class SubscriptionStorage implements SubscriptionService {

    Logger LOGGER = Logger.getLogger(ApartmentSubscription.class.getName());

    @Autowired
    @Qualifier("apartmentStorageObservable")
    Observable<Apartment> observable;

    @Autowired
    SubscriptionNotificator notificator;

    Map<Long, ApartmentSubscription> subscriptions = new ConcurrentHashMap<>();

    public boolean subscribe(Long chatId) {
        if (subscriptions.containsKey(chatId)) {
            return false;
        }
        ApartmentSubscription subscription = createSubscriber(chatId);
        subscribe(subscription);
        LOGGER.info("Subscribing " + chatId);
        subscriptions.put(chatId, subscription);
        return true;
    }

    public boolean unsubscribe(Long chatId) {
        if (!subscriptions.containsKey(chatId)) {
            return false;
        }
        subscriptions.remove(chatId).dispose();
        return true;
    }

    private void subscribe(ApartmentSubscription subscription) {
        observable
                .distinct(Apartment::getLink)
                .filter(a -> filteringPredicate(a, subscription))
                .delay(5, TimeUnit.SECONDS)
                //more filters here
                .subscribe(subscription);
    }

    private boolean filteringPredicate(Apartment apartment, ApartmentSubscription subscription) {
        return executeAndLog(apartment, subscription);
    }

    private boolean executeAndLog(Apartment apartment, ApartmentSubscription subscription) {
        boolean after = apartment.getCreatedAt().after(subscription.getStartingDate());
        if (!after) {
            LOGGER.config(apartment.getLink() + " was created before " + subscription.getStartingDate().toString());
        }
        return after;
    }

    private ApartmentSubscription createSubscriber(Long chatId) {
        return new ApartmentSubscription(chatId, text -> notificator.notify(chatId, text));
    }
}
