package com.benderski.hata.subscription;

import java.util.Date;

public interface Subscription {

    Long getId();
    Date getStartingDate();
    default int getMinPrice() {
        return 280;
    }
    default int getMaxPrice() {
        return 450;
    }
    default int getMinRoomNumber() {
        return 0;
    }
    default int getMaxRoomNumber() {
        return 8;
    }
}
