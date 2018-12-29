package com.benderski.hata.subscription;

import com.benderski.hata.infrastructure.Apartment;
import com.benderski.hata.infrastructure.StorageDao;
import com.sun.xml.internal.ws.resources.SenderMessages;
import io.reactivex.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


@Service
public class SubscriptionStorage implements SubscriptionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionStorage.class);

    private final Map<Integer, ApartmentSubscription> subscriptionMap = new HashMap<>();

    @Autowired
    private StorageDao storageDao;

    @Autowired
    @Qualifier("apartmentStorageObservable")
    private Observable<Apartment> observable;

    @Autowired
    private FilterProducer filterProducer;

    public SubscriptionModel createOrGetProfile(Integer userId) {
        return createOrRetrieveProfile(userId);
    }

    @Override
    public SubscriptionModel updateProfile(Integer userId, SubscriptionModel model) {
        return storageDao.updateProfile(userId, model);
    }

    @Override
    public SubscriptionModel getProfile(Integer userId) {
        return storageDao.getProfile(userId);
    }

    @Override
    public boolean startSubscription(Integer userId, Consumer<String> sendMessage) {
        SubscriptionModel profile = getProfile(userId);
        profile.setSubscriptionStartedDate(new Date());
        profile = storageDao.updateProfile(userId, profile);
        ApartmentSubscription subscription = new ApartmentSubscription(userId, profile, sendMessage);
        subscribe(subscription);
        subscriptionMap.put(userId, subscription);
        return false;
    }

    @Override
    public boolean unsubscribe(Integer userId) {
        ApartmentSubscription toRemove = subscriptionMap.remove(userId);
        if (toRemove != null) {
            toRemove.dispose();
            return true;
        }
        return false;
    }

    @Override
    public void commit() {
        storageDao.commit();
    }


    protected void subscribe(ApartmentSubscription subscription) {
        LOGGER.info("Subscribing " + subscription.getUserId());
        Observable<Apartment> apartmentObservable = applyFilters(observable.distinct(Apartment::getLink), subscription)
                .delay(5, TimeUnit.SECONDS);

        //more filters here
        apartmentObservable.subscribe(subscription);
    }

    private Observable<Apartment> applyFilters(Observable<Apartment> observable, Subscription subscription) {
        return observable
                .filter(a -> filterProducer.dateNotBeforeFilter(subscription).test(a.lastUpdateAt()))
                .filter(a -> filterProducer.priceFilter(subscription).test(
                        Optional.ofNullable(a.getPriceInUSD()).map(BigDecimal::intValue).orElse(null)))
                .filter(a -> filterProducer.roomNumberFilter(subscription).test(a.numberOfRoom()));
    }

    private SubscriptionModel createOrRetrieveProfile(Integer userId) {
        return storageDao.createOrRetrieveSubscription(userId);
    }
}
