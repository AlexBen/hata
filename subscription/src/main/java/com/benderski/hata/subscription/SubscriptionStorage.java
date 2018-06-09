package com.benderski.hata.subscription;

import com.benderski.hata.infrastructure.Apartment;
import com.benderski.hata.subscription.filter.DateNotBeforeFilterFactory;
import com.benderski.hata.subscription.filter.PriceFilterFactory;
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
                .filter(a -> DateNotBeforeFilterFactory.construct(subscription.getStartingDate()).test(a.lastUpdateAt()))
                .filter(a -> PriceFilterFactory.construct(280, 420).test(a.getPriceInUSD()))
                .delay(5, TimeUnit.SECONDS)
                //more filters here
                .subscribe(subscription);
    }

    private ApartmentSubscription createSubscriber(Long chatId) {
        return new ApartmentSubscription(chatId, text -> notificator.notify(chatId, text));
    }
}
