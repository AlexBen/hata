package com.benderski.hata.subscription;

import com.benderski.hata.infrastructure.Apartment;

import io.reactivex.observers.DisposableObserver;
import org.springframework.lang.NonNull;

import java.util.Date;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class ApartmentSubscription extends DisposableObserver<Apartment> implements Subscription {

    private Logger LOGGER = Logger.getLogger(ApartmentSubscription.class.getName());

    private final Long chatId;
    private SubscriptionModel model;
    private Consumer<String> notifyFunction;

    ApartmentSubscription(Long id, Consumer<String> notifyFunction, @NonNull SubscriptionModel model) {
        this.notifyFunction = notifyFunction;
        this.chatId = id;
        this.model = model;
    }

    @Override
    public Long getId() {
        return chatId;
    }

    @Override
    public Date getStartingDate() {
        return model.getSubscriptionCreatedDate();
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
        LOGGER.info("Subscription for " + chatId + " is completed");
    }
}
