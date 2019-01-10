package com.benderski.hata.subscription;

import com.benderski.hata.subscription.filter.DateNotBeforeFilterFactory;
import com.benderski.hata.subscription.filter.PriceFilterFactory;
import com.benderski.hata.subscription.filter.RoomNumberFilterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;
import java.util.function.Predicate;

@Service
public class FilterProducer {

    @Autowired
    private PriceFilterFactory priceFilterFactory;

    @Autowired
    private DateNotBeforeFilterFactory dateNotBeforeFilterFactory;

    @Autowired
    private RoomNumberFilterFactory roomNumberFilterFactory;

    public Predicate<Integer> priceFilter(Subscription subscription) {
        return priceFilterFactory.construct(
                Optional.ofNullable(subscription.getMinPrice()).orElse(0),
                Optional.ofNullable(subscription.getMaxPrice()).orElse(10000));
    }

    public Predicate<Date> dateNotBeforeFilter(Subscription subscription) {
        return dateNotBeforeFilterFactory.construct(subscription.getStartingDate());
    }

    public Predicate<Integer> roomNumberFilter(Subscription subscription) {
        return roomNumberFilterFactory.construct(
                Optional.ofNullable(subscription.getMinRoomNumber()).orElse(0),
                Optional.ofNullable(subscription.getMaxRoomNumber()).orElse(7));
    }
}
