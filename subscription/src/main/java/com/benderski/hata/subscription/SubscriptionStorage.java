package com.benderski.hata.subscription;

import com.benderski.hata.infrastructure.Apartment;
import com.benderski.hata.infrastructure.StorageDao;
import io.reactivex.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;


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
    public boolean hasSubscription(Integer userId) {
        return subscriptionMap.containsKey(userId);
    }

    @Override
    public boolean startSubscription(Integer userId, Consumer<String> sendMessage) {
        if(subscriptionMap.containsKey(userId)){
            return false;
        }
        SubscriptionModel profile = getProfile(userId);
        if (profile == null) {
            return false;
        }
        Date oneHourBefore = new Date(new Date().getTime() - 1 * 60 * 60 * 1000);
        profile.setSubscriptionStartedDate(oneHourBefore);
        SubscriptionModel savedProfile = storageDao.updateProfile(userId, profile);
        final ApartmentSubscription subscription = new ApartmentSubscription(userId, savedProfile, sendMessage);
        subscribe(subscription);
        subscriptionMap.put(userId, subscription);
        return true;
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
                .filter(a -> isNewOrUpdatedAfterWhile(a, subscription))
                .filter(a -> filterProducer.priceFilter(subscription).test(
                        Optional.ofNullable(a.getPriceInUSD()).map(BigDecimal::intValue).orElse(null)))
                .filter(a -> filterProducer.roomNumberFilter(subscription).test(a.numberOfRoom()));
    }

    //tests if apartment newly created OR just updated && created more than a month ago
    private boolean isNewOrUpdatedAfterWhile(Apartment apartment, Subscription subscription) {
        Supplier<Boolean> oldUpdated = () ->
                (filterProducer.dateNotBeforeFilter(subscription).test(apartment.lastUpdateAt()) &&
                        apartment.getCreatedAt().before(getMonthAgo()));

        return filterProducer.dateNotBeforeFilter(subscription).test(apartment.getCreatedAt())
                || oldUpdated.get();
    }

    private static Date getMonthAgo() {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MONTH, -1);
        return instance.getTime();
    }

    private SubscriptionModel createOrRetrieveProfile(Integer userId) {
        return storageDao.createOrRetrieveSubscription(userId);
    }
}
