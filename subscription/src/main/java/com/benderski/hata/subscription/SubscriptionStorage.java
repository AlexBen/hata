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

    private static final Logger LOGGER = Logger.getLogger(ApartmentSubscription.class.getName());

    @Autowired
    @Qualifier("apartmentStorageObservable")
    private Observable<Apartment> observable;

    @Autowired
    private SubscriptionNotificator notificator;

    @Autowired
    private FilterProducer filterProducer;

    private final Map<Long, ApartmentSubscription> subscriptions = new ConcurrentHashMap<>();

    public boolean subscribe(Long chatId) {
        if (subscriptions.containsKey(chatId)) {
            return false;
        }
        ApartmentSubscription subscription = createSubscriber(chatId);
        subscribe(subscription);
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

    protected void subscribe(ApartmentSubscription subscription) {
        LOGGER.info("Subscribing " + subscription.getId());
        applyFilters(observable
                .distinct(Apartment::getLink), subscription)
                .delay(5, TimeUnit.SECONDS)
                //more filters here
                .subscribe(subscription);
    }

    private Observable<Apartment> applyFilters(Observable<Apartment> observable, Subscription subscription) {
        return observable
                .filter(a -> filterProducer.dateNotBeforeFilter(subscription).test(a.lastUpdateAt()))
                .filter(a -> filterProducer.priceFilter(subscription).test(a.getPriceInUSD()))
                .filter(a -> filterProducer.roomNumberFilter(subscription).test(a.numberOfRoom()));
    }

    private ApartmentSubscription createSubscriber(Long chatId) {
        return new ApartmentSubscription(chatId, text -> notificator.notify(chatId, text));
    }
}
