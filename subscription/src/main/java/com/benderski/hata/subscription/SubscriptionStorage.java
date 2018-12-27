package com.benderski.hata.subscription;

import com.benderski.hata.infrastructure.Apartment;
import com.benderski.hata.infrastructure.StorageDao;
import io.reactivex.Observable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service
public class SubscriptionStorage implements SubscriptionService {

    private static final Logger LOGGER = Logger.getLogger(ApartmentSubscription.class.getName());

    @Autowired
    private StorageDao storageDao;

    @Autowired
    @Qualifier("apartmentStorageObservable")
    private Observable<Apartment> observable;

    @Autowired
    private FilterProducer filterProducer;

    public SubscriptionModel createProfile(Integer userId) {
        return createOrRetrieveProfile(userId);
    }

    @Override
    public SubscriptionModel getProfile(Integer userId) {
        return storageDao.getProfile(userId);
    }

    @Override
    public boolean startSubscription(Integer userId) {
        return false;
    }

    @Override
    public boolean unsubscribe(Integer userId) {
        return false;
    }


    protected void subscribe(ApartmentSubscription subscription) {
        LOGGER.info("Subscribing " + subscription.getId());
        applyFilters(observable.distinct(Apartment::getLink), subscription)
                .delay(5, TimeUnit.SECONDS)
                //more filters here
                .subscribe(subscription);
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
