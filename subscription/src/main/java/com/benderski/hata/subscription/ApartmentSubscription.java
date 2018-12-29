package com.benderski.hata.subscription;

import com.benderski.hata.infrastructure.Apartment;

import io.reactivex.observers.DisposableObserver;
import org.springframework.lang.NonNull;

import java.util.Date;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class ApartmentSubscription extends DisposableObserver<Apartment> implements Subscription {

    private Logger LOGGER = Logger.getLogger(ApartmentSubscription.class.getName());

    private final Integer userId;
    private SubscriptionModel model;
    private Consumer<String> notifyFunction;

    ApartmentSubscription(Integer id, @NonNull SubscriptionModel model, Consumer<String> notifyFunction) {
        this.notifyFunction = notifyFunction;
        this.userId = id;
        this.model = model;
    }

    @Override
    public Integer getUserId() {
        return userId;
    }

    @Override
    public Date getStartingDate() {
        return model.getSubscriptionStartedDate();
    }

    @Override
    public Integer getMinPrice() {
        return model.getMinPrice();
    }

    @Override
    public Integer getMaxPrice() {
        return model.getMaxPrice();
    }

    @Override
    public Integer getMinRoomNumber() {
        return model.getMinNumberOfRooms();
    }

    @Override
    public Integer getMaxRoomNumber() {
        return model.getMaxNumberOfRooms();
    }

    @Override
    public void onNext(Apartment apartment) {
        notifyFunction.accept(apartment.getLink());
    }

    @Override
    public void onError(Throwable e) {
        LOGGER.severe(e.getLocalizedMessage());
    }

    @Override
    public void onComplete() {
        LOGGER.info("Subscription for " + userId + " is completed");
    }
}
