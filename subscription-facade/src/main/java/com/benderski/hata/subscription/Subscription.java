package com.benderski.hata.subscription;

import java.util.Date;

public interface Subscription {

    Integer getUserId();
    Date getStartingDate();
    Integer getMinPrice();
    Integer getMaxPrice();
    Integer getMinRoomNumber();
    Integer getMaxRoomNumber();
}
