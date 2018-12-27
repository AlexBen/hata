package com.benderski.hata.subscription;

import java.util.Date;

public interface Subscription {

    Long getId();
    Date getStartingDate();
    Integer getMinPrice();
    Integer getMaxPrice();
    Integer getMinRoomNumber();
    Integer getMaxRoomNumber();
}
