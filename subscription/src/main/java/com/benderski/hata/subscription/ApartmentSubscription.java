package com.benderski.hata.subscription;

import java.util.Date;

public class ApartmentSubscription implements Subscription {

    private final Long chatId;
    private Date startingDate;

    ApartmentSubscription(Long id) {
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
}
