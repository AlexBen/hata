package com.benderski.hata.subscription;

import com.benderski.hata.infrastructure.Apartment;

import io.reactivex.observers.DisposableObserver;

import java.util.Date;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class ApartmentSubscription extends DisposableObserver<Apartment> implements Subscription {

    Logger LOGGER = Logger.getLogger(ApartmentSubscription.class.getName());

    private final Long chatId;
    private Date startingDate;
    private Consumer<String> notifyFunction;

    ApartmentSubscription(Long id, Consumer<String> notifyFunction) {
        this.notifyFunction = notifyFunction;
        this.chatId = id;
        this.startingDate = new Date();
    }

    @Override
    public Long getId() {
        return chatId;
    }

    @Override
    public Date getStartingDate() {
        return startingDate;
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
